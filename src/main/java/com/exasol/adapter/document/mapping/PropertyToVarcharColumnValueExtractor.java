package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.sql.expression.NullLiteral;
import com.exasol.sql.expression.StringLiteral;
import com.exasol.sql.expression.ValueExpression;

/**
 * ValueMapper for {@link PropertyToVarcharColumnMapping}
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public abstract class PropertyToVarcharColumnValueExtractor<DocumentVisitorType>
        extends AbstractPropertyToColumnValueExtractor<DocumentVisitorType> {
    private final PropertyToVarcharColumnMapping column;

    /**
     * Create an instance of {@link PropertyToVarcharColumnValueExtractor}.
     * 
     * @param column {@link PropertyToVarcharColumnMapping}
     */
    public PropertyToVarcharColumnValueExtractor(final PropertyToVarcharColumnMapping column) {
        super(column);
        this.column = column;
    }

    @Override
    protected final ValueExpression mapValue(final DocumentNode<DocumentVisitorType> documentValue) {
        final MappedStringResult result = mapStringValue(documentValue);
        final String stringResult = handleResult(result);
        if (stringResult == null) {
            return NullLiteral.nullLiteral();
        } else {
            return StringLiteral.of(stringResult);
        }
    }

    private String handleResult(final MappedStringResult result) {
        if (result == null) {
            return handleNotConvertedResult();
        } else {
            return handleConvertedResult(result);
        }
    }

    private String handleConvertedResult(final MappedStringResult stringValue) {
        if (stringValue.wasConverted()
                && this.column.getNonStringBehaviour().equals(ConvertableMappingErrorBehaviour.ABORT)) {
            throw new ColumnValueExtractorException(
                    "An input value is not a string. This adapter could convert it to string, but it is disabled because 'nonStringBehaviour' setting is set to ABORT.",
                    this.column);
        } else if (stringValue.wasConverted()
                && this.column.getNonStringBehaviour().equals(ConvertableMappingErrorBehaviour.NULL)) {
            return null;
        } else {
            return handleOverflowIfNecessary(stringValue.getValue());
        }
    }

    private String handleNotConvertedResult() {
        if (this.column.getNonStringBehaviour() == ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT
                || this.column.getNonStringBehaviour() == ConvertableMappingErrorBehaviour.ABORT) {
            throw new ColumnValueExtractorException("An input value could not be converted to string. "
                    + "You can either change the value in your input data, or change the nonStringBehaviour of column "
                    + this.column.getExasolColumnName() + " to NULL or CONVERT_OR_NULL.", this.column);
        } else {
            return null;
        }
    }

    protected abstract MappedStringResult mapStringValue(DocumentNode<DocumentVisitorType> dynamodbProperty);

    private String handleOverflowIfNecessary(final String sourceString) {
        if (sourceString == null) {
            return null;
        } else if (sourceString.length() > this.column.getVarcharColumnSize()) {
            return handleOverflow(sourceString);
        } else {
            return sourceString;
        }
    }

    private String handleOverflow(final String tooLongSourceString) {
        if (this.column.getOverflowBehaviour() == TruncateableMappingErrorBehaviour.TRUNCATE) {
            return tooLongSourceString.substring(0, this.column.getVarcharColumnSize());
        } else {
            throw new OverflowException(
                    "String overflow. You can either increase the string size if this column or set the overflow behaviour to truncate.",
                    this.column);
        }
    }

    /**
     * This class is used to pass the result of {@link #mapStringValue(DocumentNode)} from the concrete implementation
     * to this abstract class.
     * 
     * @implNote public so that it is accessible from test code
     */
    public static class MappedStringResult {
        private final String value;
        private final boolean isConverted;

        public MappedStringResult(final String value, final boolean isConverted) {
            this.value = value;
            this.isConverted = isConverted;
        }

        public String getValue() {
            return this.value;
        }

        public boolean wasConverted() {
            return this.isConverted;
        }
    }
}
