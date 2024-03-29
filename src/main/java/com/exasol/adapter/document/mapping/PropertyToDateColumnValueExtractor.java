package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour.*;
import static com.exasol.adapter.document.mapping.ExcerptGenerator.getExcerpt;

import java.sql.Date;
import java.util.Set;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.errorreporting.ExaError;

/**
 * This class extracts {@code DATE} values from document data. The extraction is defined using a
 * {@link PropertyToDateColumnMapping}.
 */
public class PropertyToDateColumnValueExtractor extends AbstractPropertyToColumnValueExtractor {
    private final PropertyToDateColumnMapping column;

    /**
     * Create a new instance of {@link PropertyToDateColumnValueExtractor}.
     *
     * @param column {@link PropertyToColumnMapping} defining the mapping
     */
    PropertyToDateColumnValueExtractor(final PropertyToDateColumnMapping column) {
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
        private final PropertyToDateColumnMapping column;
        private Object result;

        public ConvertVisitor(final PropertyToDateColumnMapping column) {
            this.column = column;
        }

        private Object getResult() {
            return result;
        }

        @Override
        public void visit(final DocumentArray array) {
            this.result = handleNotDate("<array>");
        }

        @Override
        public void visit(final DocumentObject object) {
            this.result = handleNotDate("<array>");
        }

        @Override
        public void visit(final DocumentNullValue nullValue) {
            this.result = null;
        }

        @Override
        public void visit(final DocumentStringValue stringValue) {
            this.result = handleNotDate("<string>");
        }

        @Override
        public void visit(final DocumentDecimalValue bigDecimalValue) {
            this.result = handleNotDateButConvertable(new Date(bigDecimalValue.getValue().longValue()),
                    "<decimal value>");
        }

        @Override
        public void visit(final DocumentBooleanValue booleanValue) {
            this.result = handleNotDate(booleanValue.getValue() ? "true" : "false");
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            this.result = handleNotDateButConvertable(new Date((long) floatingPointValue.getValue()),
                    "<floating point value>");
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            this.result = handleNotDate("<binary>");
        }

        @Override
        public void visit(final DocumentDateValue dateValue) {
            this.result = dateValue.getValue();
        }

        @Override
        public void visit(final DocumentTimestampValue timestampValue) {
            this.result = handleNotDateButConvertable(new Date(timestampValue.getValue().getTime()),
                    "<timestamp: " + timestampValue.getValue() + ">");
        }

        private Object handleNotDateButConvertable(final Date converted, final String value) {
            if (Set.of(CONVERT_OR_ABORT, CONVERT_OR_NULL).contains(this.column.getNotDateBehaviour())) {
                return converted;
            } else {
                return handleNotDate(value);
            }
        }

        private Object handleNotDate(final String value) {
            if (Set.of(ABORT, CONVERT_OR_ABORT).contains(this.column.getNotDateBehaviour())) {
                throw new ColumnValueExtractorException(
                        ExaError.messageBuilder("E-VSD-79")
                                .message("Could not convert {{VALUE}} to date column ({{COLUMN_NAME}}).")
                                .parameter("VALUE", getExcerpt(value), "An excerpt of that value.")//
                                .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                                .mitigation("Try using a different mapping.")
                                .mitigation("Ignore this error by setting 'notDateBehavior' to 'null'.").toString(),
                        this.column);
            } else {
                return null;
            }
        }
    }
}
