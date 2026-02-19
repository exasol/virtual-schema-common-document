package com.exasol.adapter.document.mapping.reader;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.edml.ExasolDocumentMappingLanguageException;
import com.exasol.adapter.document.edml.deserializer.EdmlDeserializer;
import com.exasol.adapter.document.edml.validator.EdmlSchemaValidator;
import com.exasol.adapter.document.mapping.SchemaMapping;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
import com.exasol.adapter.document.mapping.TableMapping;
import com.exasol.adapter.document.mapping.auto.SchemaInferencer;
import com.exasol.adapter.document.mapping.converter.MappingConversionPipeline;
import com.exasol.adapter.document.properties.EdmlInput;
import com.exasol.errorreporting.ExaError;
import jakarta.json.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.stream.Collectors.*;

/**
 * EDML: This class reads a {@link SchemaMapping} from JSON files.
 * <p>
 * The JSON files must follow the schema defined in {@code resources/schemas/edml-1.5.0.json}. Documentation of schema
 * mapping definitions can be found at {@code doc/user_guide/edml_user_guide.md}.
 * </p>
 */
public class JsonSchemaMappingReader {

    private final MappingConversionPipeline mappingConversionPipeline;
    private final EdmlSchemaValidator edmlSchemaValidator;
    private final EdmlDeserializer edmlDeserializer;

    /**
     * Create an instance of {@link JsonSchemaMappingReader}.
     *
     * @param tableKeyFetcher  remote database specific {@link TableKeyFetcher}
     * @param schemaInferencer mapping auto inferencer
     * @throws ExasolDocumentMappingLanguageException if schema mapping invalid
     */
    public JsonSchemaMappingReader(final TableKeyFetcher tableKeyFetcher, final SchemaInferencer schemaInferencer) {
        this.mappingConversionPipeline = new MappingConversionPipeline(tableKeyFetcher, schemaInferencer);
        this.edmlSchemaValidator = new EdmlSchemaValidator();
        this.edmlDeserializer = new EdmlDeserializer();
    }

    /**
     * Read the schema mapping.
     *
     * @param edmlInputs EDML inputs
     * @return read schema mappings
     */
    public SchemaMapping readSchemaMapping(final List<EdmlInput> edmlInputs) {
        final List<TableMapping> tables = new ArrayList<>();
        // validate and parse schema definitions for all the edml inputs
        for (final EdmlInput edmlInput : edmlInputs) {
            try {
                tables.addAll(validateAndParseDefinition(edmlInput));
            } catch (final ExasolDocumentMappingLanguageException exception) {
                throw getParseFailedException(edmlInput.getSource(), exception);
            }
        }

        validateTableMappings(tables);
        return new SchemaMapping(tables);
    }

    private ExasolDocumentMappingLanguageException getParseFailedException(final String source,
            final ExasolDocumentMappingLanguageException exception) {
        return new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("F-VSD-81")
                .message("Semantic-validation error in schema mapping {{mapping definition}}.", source).toString(),
                exception);
    }

    /** Make a list of table mappings from the EDML input */
    private List<TableMapping> validateAndParseDefinition(final EdmlInput edmlInput) {
        final String edmlString = edmlInput.getEdmlString();
        try (final StringReader reader = new StringReader(edmlString);
                final JsonReader jsonReader = Json.createReader(reader)) {
            final JsonStructure jsonStructure = jsonReader.read();
            if (isArray(jsonStructure)) {
                return validateAndParseJsonArray(jsonStructure.asJsonArray());
            } else {
                return validateAndParseJsonObject(jsonStructure.asJsonObject());
            }
        }
    }

    private boolean isArray(JsonStructure jsonStructure) {
        return jsonStructure.getValueType() == jakarta.json.JsonValue.ValueType.ARRAY;
    }

    private List<TableMapping> validateAndParseJsonArray(final JsonArray jsonArray) {
        final List<TableMapping> result = new ArrayList<>();
        for (final JsonValue jsonValue : jsonArray) {
            result.addAll(validateAndParseJsonObject(jsonValue));
        }
        return result;
    }

    private List<TableMapping> validateAndParseJsonObject(final JsonValue jsonObject) {
        final String edmlString = jsonObject.toString();
        edmlSchemaValidator.validate(edmlString);
        final EdmlDefinition definition = edmlDeserializer.deserialize(edmlString);
        return mappingConversionPipeline.convert(definition);
    }

    private void validateTableMappings(final List<TableMapping> tables) {
        final Set<String> duplicateDestinationTableNames = tables.stream()
                .collect(groupingBy(TableMapping::getExasolName, counting())) //
                .entrySet().stream() //
                .filter(entry -> entry.getValue() > 1) //
                .map(Entry::getKey) //
                .collect(toSet());
        if (!duplicateDestinationTableNames.isEmpty()) {
            throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("E-VSD-104")
                    .message("Found duplicate destination table names {{table names}}.", duplicateDestinationTableNames)
                    .mitigation("Ensure that each mapping uses a unique value for 'destinationTable'.").toString());
        }
    }
}
