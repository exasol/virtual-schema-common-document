package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT;
import static com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour.CONVERT_OR_NULL;
import static com.exasol.adapter.document.mapping.ExcerptGenerator.getExcerpt;

import java.sql.Timestamp;
import java.util.Set;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.NullLiteral;
import com.exasol.sql.expression.literal.TimestampLiteral;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This class extracts {@code TIMESTAMP} or {@code TIMESTAMP WITH LOCAL TIMEZONE} values from document data. The
 * extraction is defined using a {@link PropertyToTimestampColumnMapping}.
 */
public class PropertyToTimestampColumnValueExtractor extends AbstractPropertyToColumnValueExtractor {
    private final PropertyToTimestampColumnMapping column;

    /**
     * Create a new instance of {@link PropertyToTimestampColumnValueExtractor}.
     *
     * @param column {@link PropertyToTimestampColumnMapping} defining the mapping
     */
    PropertyToTimestampColumnValueExtractor(final PropertyToTimestampColumnMapping column) {
        super(column);
        this.column = column;
    }

    @Override
    protected ValueExpression mapValue(final DocumentNode documentValue) {
        final ConvertVisitor visitor = new ConvertVisitor(this.column);
        documentValue.accept(visitor);
        return visitor.getResult();
    }

    @RequiredArgsConstructor
    private static class ConvertVisitor implements DocumentNodeVisitor {
        private final PropertyToTimestampColumnMapping column;
        @Getter
        private ValueExpression result;

        @Override
        public void visit(final DocumentArray array) {
            this.result = handleNotTimestamp("<array>");
        }

        @Override
        public void visit(final DocumentObject object) {
            this.result = handleNotTimestamp("<array>");
        }

        @Override
        public void visit(final DocumentNullValue nullValue) {
            this.result = NullLiteral.nullLiteral();
        }

        @Override
        public void visit(final DocumentStringValue stringValue) {
            this.result = handleNotTimestamp("<string>");
        }

        @Override
        public void visit(final DocumentDecimalValue bigDecimalValue) {
            this.result = handleNotTimestampButConvertAble(
                    TimestampLiteral.of(new Timestamp(bigDecimalValue.getValue().longValue())), "<decimal value>");
        }

        @Override
        public void visit(final DocumentBooleanValue booleanValue) {
            this.result = handleNotTimestamp(booleanValue.getValue() ? "true" : "false");
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            this.result = handleNotTimestampButConvertAble(
                    TimestampLiteral.of(new Timestamp((long) floatingPointValue.getValue())), "<floating point value>");
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            this.result = handleNotTimestamp("<binary>");
        }

        @Override
        public void visit(final DocumentDateValue dateValue) {
            final TimestampLiteral converted = TimestampLiteral.of(new Timestamp(dateValue.getValue().getTime()));
            this.result = handleNotTimestampButConvertAble(converted,
                    "<date: " + dateValue.getValue().toString() + ">");
        }

        @Override
        public void visit(final DocumentTimestampValue timestampValue) {
            this.result = TimestampLiteral.of(timestampValue.getValue());
        }

        private ValueExpression handleNotTimestampButConvertAble(final ValueExpression converted, final String value) {
            if (Set.of(CONVERT_OR_ABORT, CONVERT_OR_NULL).contains(this.column.getNotTimestampBehaviour())) {
                return converted;
            } else {
                return handleNotTimestamp(value);
            }
        }

        private ValueExpression handleNotTimestamp(final String value) {
            if (this.column.getNotTimestampBehaviour() == ConvertableMappingErrorBehaviour.ABORT) {
                throw new ColumnValueExtractorException(
                        ExaError.messageBuilder("E-VSD-80")
                                .message("Could not convert {{VALUE}} to timestamp column ({{COLUMN_NAME}}).")
                                .parameter("VALUE", getExcerpt(value), "An excerpt of that value.")//
                                .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                                .mitigation("Try using a different mapping.")
                                .mitigation("Ignore this error by setting 'notTimestampBehavior' to 'null'.").toString(),
                        this.column);
            } else {
                return NullLiteral.nullLiteral();
            }
        }
    }
}
