package com.exasol.adapter.document.mapping.reader;

import com.exasol.adapter.document.mapping.MappingErrorBehaviour;
import com.exasol.adapter.document.mapping.TruncateableMappingErrorBehaviour;

/**
 * Constants defined by the Exasol Document Mapping Language (EDML) defined in
 * /src/main/resources/schemas/edml-VERSION.json
 */
class EdmlConstants {
    private EdmlConstants() {
        // empty on purpose
    }

    static final String DEST_TABLE_NAME_KEY = "destinationTable";
    static final String VARCHAR_COLUMN_SIZE_KEY = "varcharColumnSize";
    static final int DEFAULT_VARCHAR_COLUMN_SIZE = 254;
    static final String OVERFLOW_BEHAVIOUR_KEY = "overflowBehaviour";
    static final String ABORT_KEY = "ABORT";
    static final String NULL_KEY = "NULL";
    static final String CONVERT_OR_ABORT_KEY = "CONVERT_OR_ABORT";
    static final String CONVERT_OR_NULL_KEY = "CONVERT_OR_NULL";
    static final String DEST_NAME_KEY = "destinationName";
    static final String REQUIRED_KEY = "required";
    static final String TO_VARCHAR_MAPPING_KEY = "toVarcharMapping";
    static final String TO_JSON_MAPPING_KEY = "toJsonMapping";
    static final String TO_DECIMAL_MAPPING_KEY = "toDecimalMapping";
    static final TruncateableMappingErrorBehaviour DEFAULT_TO_STRING_OVERFLOW = TruncateableMappingErrorBehaviour.TRUNCATE;
    static final MappingErrorBehaviour DEFAULT_LOOKUP_BEHAVIOUR = MappingErrorBehaviour.NULL;
    static final String DECIMAL_PRECISION_KEY = "decimalPrecision";
    static final String DECIMAL_SCALE_KEY = "decimalScale";
    static final int DEFAULT_DECIMAL_SCALE = 0;
    static final int DEFAULT_DECIMAL_PRECISION = 18;
    static final String NOT_NUMERIC_BEHAVIOUR = "notNumericBehaviour";
    static final String NON_STRING_BEHAVIOUR = "nonStringBehaviour";
    static final String MAPPING_KEY = "mapping";
    static final String ADD_SOURCE_AS_COLUMN_KEY = "addSourceReferenceColumn";
}
