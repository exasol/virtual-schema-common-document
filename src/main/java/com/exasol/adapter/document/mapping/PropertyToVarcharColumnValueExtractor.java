package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.errorreporting.ExaError;
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
        final ConversionResult result = mapStringValue(documentValue);
        final String stringResult = handleResult(result);
        if (stringResult == null) {
            return NullLiteral.nullLiteral();
        } else {
            return StringLiteral.of(stringResult);
        }
    }

    private String handleResult(final ConversionResult result) {
        if (result instanceof MappedStringResult) {
            return handleConvertedResult((MappedStringResult) result);
        } else {
            return handleNotConvertedResult((CouldNotConvertResult) result);
        }
    }

    private String handleConvertedResult(final MappedStringResult stringValue) {
        if (stringValue.isConverted()
                && this.column.getNonStringBehaviour().equals(ConvertableMappingErrorBehaviour.ABORT)) {
            throw new ColumnValueExtractorException(ExaError.messageBuilder("E-VSD-36").message(
                    "The input value {{VALUE}} is not a string. This adapter could convert it to string, but it is disabled because 'nonStringBehaviour' setting is set to ABORT.")
                    .parameter("VALUE", getExcerpt(stringValue.getValue()), "An excerpt of the input value.")
                    .mitigation("Set 'nonStringBehaviour' to CONVERT_OR_ABORT or CONVERT_OR_NULL.")
                    .mitigation("Change your input data to strings.").toString(), this.column);
        } else if (stringValue.isConverted()
                && this.column.getNonStringBehaviour().equals(ConvertableMappingErrorBehaviour.NULL)) {
            return null;
        } else {
            return handleOverflowIfNecessary(stringValue.getValue());
        }
    }

    private String handleNotConvertedResult(final CouldNotConvertResult result) {
        if (this.column.getNonStringBehaviour() == ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT
                || this.column.getNonStringBehaviour() == ConvertableMappingErrorBehaviour.ABORT) {
            throw new ColumnValueExtractorException(ExaError.messageBuilder("E-VSD-37")
                    .message("An input value of type {{TYPE}} for column {{COLUMN}} could not be converted to string.")
                    .parameter("TYPE", result.getTypeName())//
                    .parameter("COLUMN", this.column.getExasolColumnName())
                    .mitigation("Change the value in your input data")
                    .mitigation("Change the nonStringBehaviour of the column to NULL or CONVERT_OR_NULL.").toString(),
                    this.column);
        } else {
            return null;
        }
    }

    protected abstract ConversionResult mapStringValue(DocumentNode<DocumentVisitorType> dynamodbProperty);

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
                    ExaError.messageBuilder("E-VSD-38").message(
                            "A value for column {{COLUMN}} exceeded the configured varcharColumnSize of {{SIZE}}.")
                            .parameter("COLUMN", this.column.getExasolColumnName())
                            .parameter("SIZE", this.column.getVarcharColumnSize())
                            .mitigation("Increase 'varcharColumnSize' for this column.")
                            .mitigation("Set 'overflowBehaviour' for this column to 'ABORT' or 'NULL'.").toString(),
                    this.column);
        }
    }

    /**
     * This interface is used to pass the result of {@link #mapStringValue(DocumentNode)} from the concrete
     * implementation to this abstract class.
     *
     * @implNote public so that it is accessible from test code
     */
    public interface ConversionResult {

    }

    public static class MappedStringResult implements ConversionResult {
        private final String value;
        private final boolean isConverted;

        public MappedStringResult(final String value, final boolean isConverted) {
            this.value = value;
            this.isConverted = isConverted;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isConverted() {
            return this.isConverted;
        }
    }

    public static class CouldNotConvertResult implements ConversionResult {
        private final String typeName;

        /**
         * Create a new instance of {@link CouldNotConvertResult}.
         * 
         * @param typeName name of the unsupported type
         */
        protected CouldNotConvertResult(final String typeName) {
            this.typeName = typeName;
        }

        /**
         * Get the name of the unsupported type.
         *
         * @return name of the unsupported type
         */
        public String getTypeName() {
            return this.typeName;
        }
    }
}
