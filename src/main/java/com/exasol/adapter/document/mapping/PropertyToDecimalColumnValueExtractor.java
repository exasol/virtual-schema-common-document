package com.exasol.adapter.document.mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.BigDecimalLiteral;
import com.exasol.sql.expression.literal.NullLiteral;

import lombok.RequiredArgsConstructor;

/**
 * This class extracts {@code DECIMAL} values from document data. The extraction is defined using a
 * {@link PropertyToDecimalColumnMapping}.
 */
public class PropertyToDecimalColumnValueExtractor extends AbstractPropertyToNumberColumnValueExtractor {

    /**
     * Create an instance of {@link PropertyToDecimalColumnValueExtractor}.
     *
     * @param column {@link PropertyToDecimalColumnMapping} defining the mapping
     */
    public PropertyToDecimalColumnValueExtractor(final PropertyToDecimalColumnMapping column) {
        super(column, new ToDecimalNumberConverter(column));
    }

    @RequiredArgsConstructor
    private static class ToDecimalNumberConverter implements NumberConverter {
        private final PropertyToDecimalColumnMapping column;

        @Override
        public ValueExpression convertString(final String stringValue) {
            return BigDecimalLiteral.of(new BigDecimal(stringValue));
        }

        @Override
        public ValueExpression convertBoolean(final boolean boolValue) {
            return BigDecimalLiteral.of(BigDecimal.valueOf(boolValue ? 1L : 0L));
        }

        @Override
        public ValueExpression convertDouble(final double doubleValue) {
            return fitBigDecimalValue(BigDecimal.valueOf(doubleValue));
        }

        @Override
        public ValueExpression convertDecimal(final BigDecimal decimalValue) {
            return fitBigDecimalValue(decimalValue);
        }

        private ValueExpression fitBigDecimalValue(final BigDecimal decimalValue) {
            final BigDecimal decimalWithDestinationScale = decimalValue.setScale(this.column.getDecimalScale(),
                    RoundingMode.FLOOR);
            if (decimalWithDestinationScale.precision() > this.column.getDecimalPrecision()) {
                return handleOverflow();
            } else {
                return BigDecimalLiteral.of(decimalWithDestinationScale);
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
    }
}
