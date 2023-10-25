package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour.*;
import static com.exasol.adapter.document.mapping.ExcerptGenerator.getExcerpt;

import java.sql.Timestamp;
import java.util.Set;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.errorreporting.ExaError;

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
    protected Object mapValue(final DocumentNode documentValue) {
        final ConvertVisitor visitor = new ConvertVisitor(this.column);
        documentValue.accept(visitor);
        return visitor.getResult();
    }

    private static class ConvertVisitor implements DocumentNodeVisitor {
        private final PropertyToTimestampColumnMapping column;
        private Object result;

        ConvertVisitor(final PropertyToTimestampColumnMapping column) {
            this.column = column;
        }

        private Object getResult() {
            return result;
        }

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
            this.result = null;
        }

        @Override
        public void visit(final DocumentStringValue stringValue) {
            this.result = handleNotTimestamp("<string>");
        }

        @Override
        public void visit(final DocumentDecimalValue bigDecimalValue) {
            this.result = handleNotTimestampButConvertable(new Timestamp(bigDecimalValue.getValue().longValue()),
                    "<decimal value>");
        }

        @Override
        public void visit(final DocumentBooleanValue booleanValue) {
            this.result = handleNotTimestamp(booleanValue.getValue() ? "true" : "false");
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            this.result = handleNotTimestampButConvertable(new Timestamp((long) floatingPointValue.getValue()),
                    "<floating point value>");
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            this.result = handleNotTimestamp("<binary>");
        }

        @Override
        public void visit(final DocumentDateValue dateValue) {
            final Timestamp converted = new Timestamp(dateValue.getValue().getTime());
            this.result = handleNotTimestampButConvertable(converted,
                    "<date: " + dateValue.getValue().toString() + ">");
        }

        @Override
        public void visit(final DocumentTimestampValue timestampValue) {
            this.result = timestampValue.getValue();
        }

        private Object handleNotTimestampButConvertable(final Timestamp converted, final String value) {
            if (Set.of(CONVERT_OR_ABORT, CONVERT_OR_NULL).contains(this.column.getNotTimestampBehaviour())) {
                return converted;
            } else {
                return handleNotTimestamp(value);
            }
        }

        private Object handleNotTimestamp(final String value) {
            if (Set.of(ABORT, CONVERT_OR_ABORT).contains(this.column.getNotTimestampBehaviour())) {
                throw new ColumnValueExtractorException(ExaError.messageBuilder("E-VSD-80")
                        .message("Could not convert {{VALUE}} to timestamp column ({{COLUMN_NAME}}).")
                        .parameter("VALUE", getExcerpt(value), "An excerpt of that value.")//
                        .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                        .mitigation("Try using a different mapping.")
                        .mitigation("Ignore this error by setting 'notTimestampBehavior' to 'null'.").toString(),
                        this.column);
            } else {
                return null;
            }
        }
    }
}
