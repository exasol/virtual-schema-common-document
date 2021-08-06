package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.ExcerptGenerator.getExcerpt;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.BigDecimalLiteral;
import com.exasol.sql.expression.literal.NullLiteral;

/**
 * This class extracts DECIMAL values from document data. The extraction is defined using a
 * {@link PropertyToDecimalColumnMapping}.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class PropertyToDecimalColumnValueExtractor extends AbstractPropertyToColumnValueExtractor {
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
    protected final ValueExpression mapValue(final DocumentNode documentValue) {
        final ConversionVisitor conversionVisitor = new ConversionVisitor(this.column);
        documentValue.accept(conversionVisitor);
        return conversionVisitor.getResult();
    }

    private static class ConversionVisitor implements DocumentNodeVisitor {
        private final PropertyToDecimalColumnMapping column;
        private ValueExpression result;

        private ConversionVisitor(final PropertyToDecimalColumnMapping column) {
            this.column = column;
        }

        @Override
        public void visit(final DocumentObject jsonObjectNode) {
            this.result = handleNotNumeric("<object>");
        }

        @Override
        public void visit(final DocumentArray jsonArrayNode) {
            this.result = handleNotNumeric("<array>");
        }

        @Override
        public void visit(final DocumentStringValue stringNode) {
            this.result = handleNotNumeric(stringNode.getValue());
        }

        @Override
        public void visit(final DocumentDecimalValue numberNode) {
            this.result = fitBigDecimalValue(numberNode.getValue());
        }

        @Override
        public void visit(final DocumentNullValue nullNode) {
            this.result = NullLiteral.nullLiteral();
        }

        @Override
        public void visit(final DocumentBooleanValue booleanNode) {
            this.result = handleNotNumeric("<" + (booleanNode.getValue() ? "true" : "false") + ">");
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            this.result = fitBigDecimalValue(BigDecimal.valueOf(floatingPointValue.getValue()));
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            this.result = handleNotNumeric("<binary data>");
        }

        private ValueExpression handleNotNumeric(final String value) {
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

        /**
         * Get the result of the conversion.
         *
         * @return result of the conversion
         */
        public ValueExpression getResult() {
            return this.result;
        }
    }
}
