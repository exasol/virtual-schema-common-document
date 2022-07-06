package com.exasol.adapter.document.mapping.reader;

import java.util.ArrayList;
import java.util.List;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.edml.ExasolDocumentMappingLanguageException;
import com.exasol.adapter.document.edml.deserializer.EdmlDeserializer;
import com.exasol.adapter.document.edml.validator.EdmlSchemaValidator;
import com.exasol.adapter.document.mapping.SchemaMapping;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
import com.exasol.adapter.document.mapping.TableMapping;
import com.exasol.adapter.document.mapping.converter.MappingConversionPipeline;
import com.exasol.adapter.document.properties.EdmlInput;
import com.exasol.errorreporting.ExaError;

/**
 * EDML: This class reads a {@link SchemaMapping} from JSON files.
 * <p>
 * The JSON files must follow the schema defined in {@code resources/schemas/edml-1.0.0.json}. Documentation of schema
 * mapping definitions can be found at {@code /doc/gettingStartedWithSchemaMappingLanguage.md}.
 * </p>
 */
public class JsonSchemaMappingReader {
    private final TableKeyFetcher tableKeyFetcher;

    /**
     * Create an instance of {@link JsonSchemaMappingReader}.
     *
     * @param tableKeyFetcher remote database specific {@link TableKeyFetcher}
     * @throws ExasolDocumentMappingLanguageException if schema mapping invalid
     */
    public JsonSchemaMappingReader(final TableKeyFetcher tableKeyFetcher) {
        this.tableKeyFetcher = tableKeyFetcher;
    }

    /**
     * Read the schema mapping.
     * 
     * @param edmlInputs EDML inputs
     * @return read schema mappings
     */
    public SchemaMapping readSchemaMapping(final List<EdmlInput> edmlInputs) {
        final EdmlSchemaValidator jsonSchemaMappingValidator = new EdmlSchemaValidator();
        final List<TableMapping> tables = new ArrayList<>();
        // validate the schema for all the edml inputs
        for (final EdmlInput edmlInput : edmlInputs) {
            jsonSchemaMappingValidator.validate(edmlInput.getEdmlString());
            try {
                tables.addAll(parseDefinition(edmlInput.getEdmlString()));
            } catch (final ExasolDocumentMappingLanguageException exception) {
                throw getParseFailedException(edmlInput.getSource(), exception);
            }
        }
        return new SchemaMapping(tables);
    }

    private ExasolDocumentMappingLanguageException getParseFailedException(final String source,
            final ExasolDocumentMappingLanguageException exception) {
        return new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("F-VSD-81")
                .message("Semantic-validation error in schema mapping {{mapping definition}}.", source).toString(),
                exception);
    }

    // make a list of tablemapping(s) from the EDML string
    private List<TableMapping> parseDefinition(final String edmlString) {
        final EdmlDefinition edmlDefinition = new EdmlDeserializer().deserialize(edmlString);
        // pipeline architecture here
        return new MappingConversionPipeline(this.tableKeyFetcher).convert(edmlDefinition);
    }
}
