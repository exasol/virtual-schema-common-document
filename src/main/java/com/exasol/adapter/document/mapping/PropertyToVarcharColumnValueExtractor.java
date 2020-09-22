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
        final Result result = mapStringValue(documentValue);
        final String stringResult = handleResult(result);
        if (stringResult == null) {
            return NullLiteral.nullLiteral();
        } else {
            return StringLiteral.of(stringResult);
        }
    }

    private String handleResult(final Result result) {
        if (result == null) {
            return handleCouldNotConvert();
        } else {
            return handleConvertedResult(result);
        }
    }

    private String handleConvertedResult(final Result stringValue) {
        if (stringValue.wasConverted()
                && this.column.getNotAStringBehaviour().equals(ConvertableMappingErrorBehaviour.ABORT)) {
            throw new ColumnValueExtractorException(
                    "An input value was not a string. This adapter could convert it to string, but you disabled this by setting notAStringBehaviour to ABORT.",
                    this.column);
        } else if (stringValue.wasConverted()
                && this.column.getNotAStringBehaviour().equals(ConvertableMappingErrorBehaviour.NULL)) {
            return null;
        } else {
            return handleOverflowIfNecessary(stringValue.getValue());
        }
    }

    private String handleCouldNotConvert() {
        if (this.column.getNotAStringBehaviour() == ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT
                || this.column.getNotAStringBehaviour() == ConvertableMappingErrorBehaviour.ABORT) {
            throw new ColumnValueExtractorException("An input value could not be converted to string. "
                    + "You can either change the value in your input data, or change the notAStringBehaviour of column "
                    + this.column.getExasolColumnName() + " to NULL or CONVERT_OR_NULL.", this.column);
        } else {
            return null;
        }
    }

    protected abstract Result mapStringValue(DocumentNode<DocumentVisitorType> dynamodbProperty);

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
    public static class Result {
        private final String value;
        private final boolean wasConverted;

        public Result(final String value, final boolean wasConverted) {
            this.value = value;
            this.wasConverted = wasConverted;
        }

        public String getValue() {
            return this.value;
        }

        public boolean wasConverted() {
            return this.wasConverted;
        }
    }
}
