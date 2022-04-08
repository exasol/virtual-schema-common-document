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
 * This class extracts {@code BOOL} values from document data. The extraction is defined using a
 * {@link PropertyToBoolColumnMapping}.
 */
public class PropertyToBoolColumnValueExtractor extends AbstractPropertyToColumnValueExtractor {
    private final PropertyToBoolColumnMapping column;

    /**
     * Create an instance of {@link AbstractPropertyToColumnValueExtractor} for extracting a value specified parameter
     * column from a DynamoDB row.
     *
     * @param column {@link PropertyToColumnMapping} defining the mapping
     */
    PropertyToBoolColumnValueExtractor(final PropertyToBoolColumnMapping column) {
        super(column);
        this.column = column;
    }

    @Override
    protected Object mapValue(final DocumentNode documentValue) {
        final ConvertVisitor visitor = new ConvertVisitor(this.column);
        documentValue.accept(visitor);
        return visitor.getResult();
    }

    @RequiredArgsConstructor
    private static class ConvertVisitor implements DocumentNodeVisitor {
        private final PropertyToBoolColumnMapping column;
        @Getter
        private Object result;

        @Override
        public void visit(final DocumentArray array) {
            this.result = handleNotBoolean("<array>");
        }

        @Override
        public void visit(final DocumentObject object) {
            this.result = handleNotBoolean("<array>");
        }

        @Override
        public void visit(final DocumentNullValue nullValue) {
            this.result = null;
        }

        @Override
        public void visit(final DocumentStringValue stringValue) {
            final boolean converted = "true".equalsIgnoreCase(stringValue.getValue());
            this.result = handleNotBooleanButConvertAble(converted, stringValue.getValue());
        }

        @Override
        public void visit(final DocumentDecimalValue bigDecimalValue) {
            final boolean converted = bigDecimalValue.getValue().compareTo(BigDecimal.ZERO) != 0;
            this.result = handleNotBooleanButConvertAble(converted, bigDecimalValue.getValue().toString());
        }

        @Override
        public void visit(final DocumentBooleanValue booleanValue) {
            this.result = booleanValue.getValue();
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            final boolean converted = floatingPointValue.getValue() != 0;
            this.result = handleNotBooleanButConvertAble(converted, String.valueOf(floatingPointValue.getValue()));
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            this.result = handleNotBoolean("<binary>");
        }

        @Override
        public void visit(final DocumentDateValue dateValue) {
            this.result = handleNotBoolean("<date>");
        }

        @Override
        public void visit(final DocumentTimestampValue timestampValue) {
            this.result = handleNotBoolean("<timestamp>");
        }

        private Object handleNotBooleanButConvertAble(final boolean converted, final String value) {
            if (Set.of(CONVERT_OR_ABORT, CONVERT_OR_NULL).contains(this.column.getNotBooleanBehavior())) {
                return converted;
            } else {
                return handleNotBoolean(value);
            }
        }

        private Object handleNotBoolean(final String value) {
            if (Set.of(ABORT, CONVERT_OR_ABORT).contains(this.column.getNotBooleanBehavior())) {
                throw new ColumnValueExtractorException(
                        ExaError.messageBuilder("E-VSD-78")
                                .message("Could not convert {{VALUE}} to boolean column ({{COLUMN_NAME}}).")
                                .parameter("VALUE", getExcerpt(value), "An excerpt of that value.")//
                                .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                                .mitigation("Try using a different mapping.")
                                .mitigation("Ignore this error by setting 'notBooleanBehavior' to 'null'.").toString(),
                        this.column);
            } else {
                return null;
            }
        }
    }
}
