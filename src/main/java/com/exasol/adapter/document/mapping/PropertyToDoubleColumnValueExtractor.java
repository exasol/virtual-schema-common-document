package com.exasol.adapter.document.mapping;

import java.math.BigDecimal;

import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.errorreporting.ExaError;

import lombok.RequiredArgsConstructor;

/**
 * This class extracts {@code DOUBLE-PRECISION} values from document data. The extraction is defined using a
 * {@link PropertyToDoubleColumnMapping}.
 */
public class PropertyToDoubleColumnValueExtractor extends AbstractPropertyToNumberColumnValueExtractor {

    /**
     * Create an instance of {@link PropertyToDoubleColumnValueExtractor}.
     *
     * @param column {@link PropertyToDoubleColumnMapping} defining the mapping
     */
    public PropertyToDoubleColumnValueExtractor(final PropertyToDoubleColumnMapping column) {
        super(column, new ToDoubleNumberConverter(column));
    }

    @RequiredArgsConstructor
    private static class ToDoubleNumberConverter implements NumberConverter {
        private final PropertyToDoubleColumnMapping column;

        @Override
        public Object convertString(final String stringValue) throws NumberFormatException {
            return Double.parseDouble(stringValue);
        }

        @Override
        public Object convertBoolean(final boolean boolValue) {
            return boolValue ? 1.0 : 0.0;
        }

        @Override
        public Object convertDouble(final double doubleValue) {
            return doubleValue;
        }

        @Override
        public Object convertDecimal(final BigDecimal decimalValue) {
            if (isInDoubleRange(decimalValue)) {
                return decimalValue.doubleValue();
            } else {
                return handleOverflow(decimalValue);
            }
        }

        private boolean isInDoubleRange(final BigDecimal decimalValue) {
            return BigDecimal.valueOf(Double.MAX_VALUE).compareTo(decimalValue) >= 0
                    && BigDecimal.valueOf(-Double.MAX_VALUE).compareTo(decimalValue) <= 0;
        }

        private Object handleOverflow(final BigDecimal value) {
            if (this.column.getOverflowBehaviour() == MappingErrorBehaviour.ABORT) {
                throw new OverflowException(ExaError.messageBuilder("E-VSD-77").message(
                        "The input value {{value}} exceeds the size of the DOUBLE-PRECISION column {{COLUMN_NAME}}.",
                        value, this.column.getExasolColumnName())
                        .mitigation("Use a toDecimalMapping with a high precision for this column.")
                        .mitigation("Set the overflow behaviour to NULL.").toString(), this.column);
            } else {
                return null;
            }
        }
    }
}
