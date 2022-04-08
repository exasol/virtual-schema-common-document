package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour.*;
import static com.exasol.adapter.document.mapping.ExcerptGenerator.getExcerpt;

import java.math.BigDecimal;
import java.util.Set;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.errorreporting.ExaError;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This class is an abstract base for classes that extract numeric values from documents.
 */
abstract class AbstractPropertyToNumberColumnValueExtractor extends AbstractPropertyToColumnValueExtractor {
    private final AbstractPropertyToNumberColumnMapping column;
    private final NumberConverter numberConverter;

    /**
     * Create an instance of {@link AbstractPropertyToNumberColumnValueExtractor}.
     *
     * @param column          {@link PropertyToDecimalColumnMapping} defining the mapping
     * @param numberConverter number converter
     */
    protected AbstractPropertyToNumberColumnValueExtractor(final AbstractPropertyToNumberColumnMapping column,
            final NumberConverter numberConverter) {
        super(column);
        this.column = column;
        this.numberConverter = numberConverter;
    }

    @Override
    protected final Object mapValue(final DocumentNode documentValue) {
        final ConversionVisitor conversionVisitor = new ConversionVisitor(this.column, this.numberConverter);
        documentValue.accept(conversionVisitor);
        return conversionVisitor.getResult();
    }

    protected interface NumberConverter {
        /**
         * Convert a string value to number.
         * 
         * @param stringValue string value to convert
         * @return number
         * @throws NumberFormatException if the string is not a number
         */
        public Object convertString(final String stringValue) throws NumberFormatException;

        /**
         * Convert a boolean to number.
         * 
         * @param boolValue boolean value to convert
         * @return number
         */
        public Object convertBoolean(final boolean boolValue);

        /**
         * Convert a double value to number.
         * 
         * @param doubleValue double value to convert
         * @return number
         */
        public Object convertDouble(final double doubleValue);

        /**
         * Convert a decimal value to number.
         * 
         * @param decimalValue decimal value to convert
         * @return number
         */
        public Object convertDecimal(final BigDecimal decimalValue);
    }

    @RequiredArgsConstructor
    private static class ConversionVisitor implements DocumentNodeVisitor {
        private final AbstractPropertyToNumberColumnMapping column;
        private final NumberConverter numberConverter;
        @Getter
        private Object result;

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
            final String stringValue = stringNode.getValue();
            try {
                final Object converted = this.numberConverter.convertString(stringValue);
                this.result = handleNotNumericButConvertable(converted, stringValue);
            } catch (final NumberFormatException exception) {
                this.result = handleNotNumeric(stringValue);
            }
        }

        @Override
        public void visit(final DocumentDecimalValue numberNode) {
            final BigDecimal decimalValue = numberNode.getValue();
            this.result = this.numberConverter.convertDecimal(decimalValue);
        }

        @Override
        public void visit(final DocumentNullValue nullNode) {
            this.result = null;
        }

        @Override
        public void visit(final DocumentBooleanValue booleanNode) {
            this.result = handleNotNumericButConvertable(this.numberConverter.convertBoolean(booleanNode.getValue()),
                    "<" + (booleanNode.getValue() ? "true" : "false") + ">");
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            final double floatValue = floatingPointValue.getValue();
            this.result = this.numberConverter.convertDouble(floatValue);
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            this.result = handleNotNumeric("<binary data>");
        }

        @Override
        public void visit(final DocumentDateValue dateValue) {
            final Object converted = this.numberConverter
                    .convertDecimal(BigDecimal.valueOf(dateValue.getValue().getTime()));
            this.result = handleNotNumericButConvertable(converted, "<date>");
        }

        @Override
        public void visit(final DocumentTimestampValue timestampValue) {
            final Object converted = this.numberConverter
                    .convertDecimal(BigDecimal.valueOf(timestampValue.getValue().getTime()));
            this.result = handleNotNumericButConvertable(converted, "<timestamp>");
        }

        private Object handleNotNumericButConvertable(final Object converted, final String value) {
            if (Set.of(CONVERT_OR_ABORT, CONVERT_OR_NULL).contains(this.column.getNotNumericBehaviour())) {
                return converted;
            } else {
                return handleNotNumeric(value);
            }
        }

        private Object handleNotNumeric(final String value) {
            if (Set.of(ABORT, CONVERT_OR_ABORT).contains(this.column.getNotNumericBehaviour())) {
                throw new ColumnValueExtractorException(
                        ExaError.messageBuilder("E-VSD-33")
                                .message("Could not convert {{VALUE}} to numeric column ({{COLUMN_NAME}}).")
                                .parameter("VALUE", getExcerpt(value), "An excerpt of that value.")//
                                .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                                .mitigation("Try using a different mapping.")
                                .mitigation("Ignore this error by setting 'notNumericBehaviour' to 'null'.").toString(),
                        this.column);
            } else {
                return null;
            }
        }
    }
}
