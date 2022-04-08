package com.exasol.adapter.document.mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.errorreporting.ExaError;

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
        public Object convertString(final String stringValue) {
            return new BigDecimal(stringValue);
        }

        @Override
        public Object convertBoolean(final boolean boolValue) {
            return BigDecimal.valueOf(boolValue ? 1L : 0L);
        }

        @Override
        public Object convertDouble(final double doubleValue) {
            return fitBigDecimalValue(BigDecimal.valueOf(doubleValue));
        }

        @Override
        public Object convertDecimal(final BigDecimal decimalValue) {
            return fitBigDecimalValue(decimalValue);
        }

        private Object fitBigDecimalValue(final BigDecimal decimalValue) {
            final BigDecimal decimalWithDestinationScale = decimalValue.setScale(this.column.getDecimalScale(),
                    RoundingMode.FLOOR);
            if (decimalWithDestinationScale.precision() > this.column.getDecimalPrecision()) {
                return handleOverflow();
            } else {
                return decimalWithDestinationScale;
            }
        }

        private Object handleOverflow() {
            if (this.column.getOverflowBehaviour() == MappingErrorBehaviour.ABORT) {
                throw new OverflowException(ExaError.messageBuilder("E-VSD-34")
                        .message("An input value exceeded the size of the DECIMAL column {{COLUMN_NAME}}.")
                        .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                        .mitigation("Increase the decimalPrecision of this column in your mapping definition.")
                        .mitigation("Set the overflow behaviour to NULL.").toString(), this.column);
            } else {
                return null;
            }
        }
    }
}
