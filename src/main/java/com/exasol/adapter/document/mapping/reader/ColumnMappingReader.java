package com.exasol.adapter.document.mapping.reader;

import javax.json.JsonObject;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.*;
import com.exasol.errorreporting.ExaError;

/**
 * This class creates {@link ColumnMapping}s from a JSON definition. It is used in the {@link JsonSchemaMappingReader}.
 */
class ColumnMappingReader {
    private static final ColumnMappingReader INSTANCE = new ColumnMappingReader();

    /**
     * Private constructor to hide the public default. Get an instance using {@link #getInstance()}.
     */
    private ColumnMappingReader() {
        // empty on purpose
    }

    /**
     * Get a singleton instance of {@link ColumnMappingReader}.
     * 
     * @return singleton instance of {@link ColumnMappingReader}
     */
    public static ColumnMappingReader getInstance() {
        return INSTANCE;
    }

    ColumnMapping readColumnMapping(final String mappingKey, final JsonObject definition,
            final DocumentPathExpression.Builder sourcePath, final String propertyName, final boolean isRootLevel) {
        return readColumnMappingBuilder(mappingKey, definition, isRootLevel)//
                .pathToSourceProperty(sourcePath.build())//
                .exasolColumnName(readExasolColumnName(definition, propertyName))//
                .lookupFailBehaviour(readLookupFailBehaviour(definition)).build();
    }

    private PropertyToColumnMapping.Builder readColumnMappingBuilder(final String mappingKey,
            final JsonObject definition, final boolean isRootLevel) {
        switch (mappingKey) {
        case EdmlConstants.TO_VARCHAR_MAPPING_KEY:
            abortIfAtRootLevel(EdmlConstants.TO_VARCHAR_MAPPING_KEY, isRootLevel);
            return readToVarcharColumn(definition);
        case EdmlConstants.TO_JSON_MAPPING_KEY:
            return readToJsonColumn(definition);
        case EdmlConstants.TO_DECIMAL_MAPPING_KEY:
            abortIfAtRootLevel(EdmlConstants.TO_DECIMAL_MAPPING_KEY, isRootLevel);
            return readToDecimalColumn(definition);
        default:
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-EDML-7")
                    .message("The mapping type {{MAPPING_TYPE}} is not supported in this version.")
                    .parameter("MAPPING_TYPE", mappingKey).ticketMitigation().toString());
        }
    }

    private PropertyToVarcharColumnMapping.Builder readToVarcharColumn(final JsonObject definition) {
        final PropertyToVarcharColumnMapping.Builder builder = PropertyToVarcharColumnMapping.builder();
        readLookupFailBehaviour(definition);
        return builder.overflowBehaviour(readStringOverflowBehaviour(definition))//
                .varcharColumnSize(readVarcharColumnSize(definition))
                .nonStringBehaviour(readConvertableMappingErrorBehaviour(definition));
    }

    private PropertyToJsonColumnMapping.Builder readToJsonColumn(final JsonObject definition) {
        return PropertyToJsonColumnMapping.builder()//
                .varcharColumnSize(readVarcharColumnSize(definition))//
                .overflowBehaviour(readMappingErrorBehaviour(EdmlConstants.OVERFLOW_BEHAVIOUR_KEY,
                        MappingErrorBehaviour.ABORT, definition));
    }

    private PropertyToDecimalColumnMapping.Builder readToDecimalColumn(final JsonObject definition) {
        return PropertyToDecimalColumnMapping.builder()//
                .decimalPrecision(
                        definition.getInt(EdmlConstants.DECIMAL_PRECISION_KEY, EdmlConstants.DEFAULT_DECIMAL_PRECISION))//
                .decimalScale(definition.getInt(EdmlConstants.DECIMAL_SCALE_KEY, EdmlConstants.DEFAULT_DECIMAL_SCALE))//
                .overflowBehaviour(readMappingErrorBehaviour(EdmlConstants.OVERFLOW_BEHAVIOUR_KEY,
                        MappingErrorBehaviour.ABORT, definition))//
                .notNumericBehaviour(readMappingErrorBehaviour(EdmlConstants.NOT_NUMERIC_BEHAVIOUR,
                        MappingErrorBehaviour.ABORT, definition));
    }

    private MappingErrorBehaviour readMappingErrorBehaviour(final String key, final MappingErrorBehaviour defaultValue,
            final JsonObject definition) {
        switch (definition.getString(key, "").toUpperCase()) {
        case EdmlConstants.ABORT_KEY:
            return MappingErrorBehaviour.ABORT;
        case EdmlConstants.NULL_KEY:
            return MappingErrorBehaviour.NULL;
        default:
            return defaultValue;
        }
    }

    private ConvertableMappingErrorBehaviour readConvertableMappingErrorBehaviour(final JsonObject definition) {
        switch (definition.getString(EdmlConstants.NON_STRING_BEHAVIOUR, "").toUpperCase()) {
        case EdmlConstants.ABORT_KEY:
            return ConvertableMappingErrorBehaviour.ABORT;
        case EdmlConstants.NULL_KEY:
            return ConvertableMappingErrorBehaviour.NULL;
        case EdmlConstants.CONVERT_OR_ABORT_KEY:
            return ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT;
        case EdmlConstants.CONVERT_OR_NULL_KEY:
            return ConvertableMappingErrorBehaviour.CONVERT_OR_NULL;
        default:
            return ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT;
        }
    }

    private int readVarcharColumnSize(final JsonObject definition) {
        return definition.getInt(EdmlConstants.VARCHAR_COLUMN_SIZE_KEY, EdmlConstants.DEFAULT_VARCHAR_COLUMN_SIZE);
    }

    private TruncateableMappingErrorBehaviour readStringOverflowBehaviour(final JsonObject definition) {
        if (definition.containsKey(EdmlConstants.OVERFLOW_BEHAVIOUR_KEY)
                && definition.getString(EdmlConstants.OVERFLOW_BEHAVIOUR_KEY).equals(EdmlConstants.ABORT_KEY)) {
            return TruncateableMappingErrorBehaviour.ABORT;
        } else {
            return EdmlConstants.DEFAULT_TO_STRING_OVERFLOW;
        }
    }

    private MappingErrorBehaviour readLookupFailBehaviour(final JsonObject definition) {
        if (definition.containsKey(EdmlConstants.REQUIRED_KEY) && definition.getBoolean(EdmlConstants.REQUIRED_KEY)) {
            return MappingErrorBehaviour.ABORT;
        } else {
            return EdmlConstants.DEFAULT_LOOKUP_BEHAVIOUR;
        }
    }

    private String readExasolColumnName(final JsonObject definition, final String defaultValue) {
        final String exasolColumnName = definition.getString(EdmlConstants.DEST_NAME_KEY, defaultValue);
        if (exasolColumnName == null) {
            throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("E-VSD-EDML-8")
                    .message("'destinationName' is mandatory in this definition.")
                    .mitigation("Please set it to the desired name for the Exasol column.").toString());
        }
        return exasolColumnName.toUpperCase();
    }

    private void abortIfAtRootLevel(final String mappingType, final boolean isRootLevel) {
        if (isRootLevel) {
            throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("E-VSD-EDML-9")
                    .message("The mapping type {{MAPPING_TYPE}} is not allowed at root level.")
                    .parameter("MAPPING_TYPE", mappingType)
                    .mitigation("You probably want to replace it with a 'fields' definition.").toString());
        }
    }
}
