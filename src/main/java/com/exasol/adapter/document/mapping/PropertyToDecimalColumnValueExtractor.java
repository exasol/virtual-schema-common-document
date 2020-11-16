package com.exasol.adapter.document.mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.BigDecimalLiteral;
import com.exasol.sql.expression.NullLiteral;
import com.exasol.sql.expression.ValueExpression;

/**
 * This class extracts DECIMAL values from document data. The extraction is defined using a
 * {@link PropertyToDecimalColumnMapping}.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public abstract class PropertyToDecimalColumnValueExtractor<DocumentVisitorType>
        extends AbstractPropertyToColumnValueExtractor<DocumentVisitorType> {
    private final PropertyToDecimalColumnMapping column;

    /**
     * Create an instance of {@link PropertyToDecimalColumnValueExtractor}.
     *
     * @param column {@link PropertyToDecimalColumnMapping} defining the mapping
     */
    public PropertyToDecimalColumnValueExtractor(final PropertyToDecimalColumnMapping column) {
        super(column);
        this.column = column;
    }

    @Override
    protected final ValueExpression mapValue(final DocumentNode<DocumentVisitorType> documentValue) {
        final ConversionResult conversionResult = mapValueToDecimal(documentValue);
        if (conversionResult instanceof ConvertedResult) {
            final ConvertedResult convertedResult = (ConvertedResult) conversionResult;
            return fitValue(convertedResult.getResult());
        } else {
            final NotANumberResult result = (NotANumberResult) conversionResult;
            return handleNotANumber(result.getValue());
        }
    }

    private ValueExpression fitValue(final BigDecimal decimalValue) {
        final BigDecimal decimalWithDestinationScale = decimalValue.setScale(this.column.getDecimalScale(),
                RoundingMode.FLOOR);
        if (decimalWithDestinationScale.precision() > this.column.getDecimalPrecision()) {
            return handleOverflow();
        } else {
            return BigDecimalLiteral.of(decimalWithDestinationScale);
        }
    }

    private ValueExpression handleNotANumber(final String value) {
        if (this.column.getNotNumericBehaviour() == MappingErrorBehaviour.ABORT) {
            throw new ColumnValueExtractorException(
                    ExaError.messageBuilder("E-VSD-33")
                            .message("Could not convert {{VALUE}} to decimal column ({{COLUMN_NAME}}).")
                            .parameter("VALUE", getExcerpt(value), "An excerpt of that value.")//
                            .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                            .mitigation("Try using a different mapping.")
                            .mitigation("Ignore this error by setting 'notNumericBehaviour' to 'null'.").toString(),
                    this.column);
        } else {
            return NullLiteral.nullLiteral();
        }
    }

    private ValueExpression handleOverflow() {
        if (this.column.getOverflowBehaviour() == MappingErrorBehaviour.ABORT) {
            throw new OverflowException(ExaError.messageBuilder("E-VSD-34")
                    .message("An input value exceeded the size of the DECIMAL column {{COLUMN_NAME}}.")
                    .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                    .mitigation("Increase the decimalPrecision of this column in your mapping definition.")
                    .mitigation("Set the overflow behaviour to NULL.").toString(), this.column);
        } else {
            return NullLiteral.nullLiteral();
        }
    }

    /**
     * Convert the document value to a BigDecimal. If not a number return {@code null}.
     * 
     * @param documentValue document value to convert
     * @return BigDecimal representation
     */
    protected abstract ConversionResult mapValueToDecimal(final DocumentNode<DocumentVisitorType> documentValue);

    protected ConversionResult parseString(final String value) {
        try {
            return new ConvertedResult(new BigDecimal(value));
        } catch (final NumberFormatException exception) {
            return new NotANumberResult(value);
        }
    }

    /**
     * Interface for the result of the conversion.
     * 
     * @implNote public so that accessible in test-code
     */
    public interface ConversionResult {

    }

    /**
     * This class represents the result of a successful conversion.
     */
    public static class ConvertedResult implements ConversionResult {
        private final BigDecimal result;

        /**
         * Create a mew instance {@link ConversionResult}.
         *
         * @param result decimal result
         */
        protected ConvertedResult(final BigDecimal result) {
            this.result = result;
        }

        private BigDecimal getResult() {
            return this.result;
        }
    }

    /**
     * Result if the value was not a number.
     */
    public static class NotANumberResult implements ConversionResult {
        private final String value;

        /**
         * Create a new instance of {@link NotANumberResult}.
         *
         * @param value string value for error message.
         */
        protected NotANumberResult(final String value) {
            this.value = value;
        }

        private String getValue() {
            return this.value;
        }
    }
}
