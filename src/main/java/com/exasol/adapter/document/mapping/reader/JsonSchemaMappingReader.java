package com.exasol.adapter.document.mapping.reader;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.json.*;

import com.exasol.adapter.document.edml.validator.EdmlSchemaValidator;
import com.exasol.adapter.document.mapping.*;
import com.exasol.errorreporting.ExaError;

/**
 * This class reads a {@link SchemaMapping} from JSON files.
 * <p>
 * The JSON files must follow the schema defined in {@code resources/schemas/edml-1.0.0.json}. Documentation of schema
 * mapping definitions can be found at {@code /doc/gettingStartedWithSchemaMappingLanguage.md}.
 * </p>
 */
public class JsonSchemaMappingReader implements SchemaMappingReader {
    private final List<TableMapping> tables = new ArrayList<>();
    private final TableKeyFetcher tableKeyFetcher;

    /**
     * Create an instance of {@link JsonSchemaMappingReader}.
     *
     * @param definitionsPath path to the definition. Can either be a {@code .json} file or an directory. If it points
     *                        to a directory, all {@code .json} files are loaded.
     * @param tableKeyFetcher remote database specific {@link TableKeyFetcher}
     * @throws ExasolDocumentMappingLanguageException if schema mapping invalid
     */
    public JsonSchemaMappingReader(final File definitionsPath, final TableKeyFetcher tableKeyFetcher) {
        this(splitIfDirectory(definitionsPath), tableKeyFetcher);
    }

    private JsonSchemaMappingReader(final File[] definitionsPaths, final TableKeyFetcher tableKeyFetcher) {
        this.tableKeyFetcher = tableKeyFetcher;
        final EdmlSchemaValidator jsonSchemaMappingValidator = new EdmlSchemaValidator();
        for (final File definitionPath : definitionsPaths) {
            try {
                jsonSchemaMappingValidator.validate(Files.readString(definitionPath.toPath()));
            } catch (final IOException exception) {
                throw new ExasolDocumentMappingLanguageException(
                        ExaError.messageBuilder("F-VSD-84").message("Failed to open {{MAPPING_FILE}}.")
                                .parameter("MAPPING_FILE", definitionPath.toString()).toString(),
                        exception);
            }
            try {
                parseFile(definitionPath);
            } catch (final ExasolDocumentMappingLanguageException exception) {
                throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("F-VSD-81")
                        .message("Semantic-validation error in schema mapping {{MAPPING_FILE}}.")
                        .parameter("MAPPING_FILE", definitionPath.toString()).toString(), exception);
            }
        }
    }

    /**
     * If the given definitionsPath is an directory all json files are returned.
     *
     * @param definitionsPath path to file or directory
     * @return array of definition files
     */
    private static File[] splitIfDirectory(final File definitionsPath) {
        if (definitionsPath.isFile()) {
            return new File[] { definitionsPath };
        } else {
            return splitDirectory(definitionsPath);
        }
    }

    private static File[] splitDirectory(final File definitionsPath) {
        final String jsonFileEnding = ".json";
        final File[] files = definitionsPath.listFiles((file, fileName) -> fileName.endsWith(jsonFileEnding));
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-21")
                    .message("No schema mapping files found in {{MAPPINGS_FOLDER}}.")
                    .parameter("MAPPINGS_FOLDER", definitionsPath)
                    .mitigation(
                            "Please check that you definition files have a .json ending and are uploaded to the BucketFS path that was specified in the MAPPING property.")
                    .toString());
        }
        return files;
    }

    private void parseFile(final File definitionPath) {
        try (final InputStream inputStream = new FileInputStream(definitionPath);
                final JsonReader reader = Json.createReader(inputStream)) {
            final JsonObject definitionObject = reader.readObject();
            this.tables.addAll(new RootTableMappingReader(definitionObject, this.tableKeyFetcher).getTables());
        } catch (final IOException exception) {
            throw new IllegalArgumentException(
                    ExaError.messageBuilder("E-VSD-24").message("Failed to open mapping file {{MAPPING_FILE}}.")
                            .parameter("MAPPING_FILE", definitionPath).toString(),
                    exception);
        }
    }

    @Override
    public SchemaMapping getSchemaMapping() {
        return new SchemaMapping(this.tables);
    }
}
