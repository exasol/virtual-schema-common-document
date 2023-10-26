package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour.*;
import static com.exasol.adapter.document.edml.TruncateableMappingErrorBehaviour.NULL;
import static com.exasol.adapter.document.edml.TruncateableMappingErrorBehaviour.TRUNCATE;
import static com.exasol.adapter.document.mapping.ExcerptGenerator.getExcerpt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.errorreporting.ExaError;

/**
 * ValueMapper for {@link PropertyToVarcharColumnMapping}
 */
public class PropertyToVarcharColumnValueExtractor extends AbstractPropertyToColumnValueExtractor {
    private final PropertyToVarcharColumnMapping column;

    /**
     * Create an instance of {@link PropertyToVarcharColumnValueExtractor}.
     * 
     * @param column {@link PropertyToVarcharColumnMapping}
     */
    public PropertyToVarcharColumnValueExtractor(final PropertyToVarcharColumnMapping column) {
        super(column);
        this.column = column;
    }

    @Override
    protected final Object mapValue(final DocumentNode documentValue) {
        final ToStringVisitor visitor = new ToStringVisitor(this.column);
        documentValue.accept(visitor);
        return handleOverflowIfNecessary(visitor.getResult());
    }

    private String handleOverflowIfNecessary(final String sourceString) {
        if (sourceString == null) {
            return null;
        } else if (sourceString.length() > this.column.getVarcharColumnSize()) {
            return handleOverflow(sourceString);
        } else {
            return sourceString;
        }
    }

    private String handleOverflow(final String tooLongSourceString) {
        if (this.column.getOverflowBehaviour() == TRUNCATE) {
            return tooLongSourceString.substring(0, this.column.getVarcharColumnSize());
        } else if (this.column.getOverflowBehaviour() == NULL) {
            return null;
        } else {
            throw new OverflowException(ExaError.messageBuilder("E-VSD-38")
                    .message("A value for column {{COLUMN}} exceeded the configured varcharColumnSize of {{SIZE}}.")
                    .parameter("COLUMN", this.column.getExasolColumnName())
                    .parameter("SIZE", this.column.getVarcharColumnSize())
                    .mitigation("Increase 'varcharColumnSize' for this column.")
                    .mitigation("Set 'overflowBehaviour' for this column to 'TRUNCATE' or 'NULL'.").toString(),
                    this.column);
        }
    }

    private static class ToStringVisitor implements DocumentNodeVisitor {
        private final PropertyToVarcharColumnMapping column;
        private String result;

        private ToStringVisitor(final PropertyToVarcharColumnMapping column) {
            this.column = column;
        }

        private String getResult() {
            return result;
        }

        @Override
        public void visit(final DocumentObject objectNode) {
            this.result = handleNotString("<object>");
        }

        @Override
        public void visit(final DocumentArray arrayNode) {
            this.result = handleNotString("<array>");
        }

        @Override
        public void visit(final DocumentStringValue stringNode) {
            this.result = stringNode.getValue();
        }

        @Override
        public void visit(final DocumentDecimalValue numberNode) {
            final String converted = numberNode.getValue().toString();
            this.result = handleNotStringButConvertable(converted);
        }

        @Override
        public void visit(final DocumentNullValue nullNode) {
            this.result = null;
        }

        @Override
        public void visit(final DocumentBooleanValue booleanNode) {
            this.result = handleNotStringButConvertable(booleanNode.getValue() ? "true" : "false");
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            final String converted = String.valueOf(floatingPointValue.getValue());
            this.result = handleNotStringButConvertable(converted);
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            final String bas64Encoded = new String(Base64.getEncoder().encode(binaryValue.getBinary()),
                    StandardCharsets.UTF_8);
            this.result = handleNotStringButConvertable(bas64Encoded);
        }

        @Override
        public void visit(final DocumentDateValue dateValue) {
            this.result = handleNotStringButConvertable(dateValue.getValue().toString());
        }

        @Override
        public void visit(final DocumentTimestampValue timestampValue) {
            this.result = handleNotStringButConvertable(timestampValue.getValue().toInstant().toString());
        }

        private String handleNotStringButConvertable(final String converted) {
            if (Set.of(CONVERT_OR_ABORT, CONVERT_OR_NULL).contains(this.column.getNonStringBehaviour())) {
                return converted;
            } else {
                return handleNotString(converted);
            }
        }

        private String handleNotString(final String value) {
            if (Set.of(ABORT, CONVERT_OR_ABORT).contains(this.column.getNonStringBehaviour())) {
                throw new ColumnValueExtractorException(ExaError.messageBuilder("E-VSD-36").message(
                        "The input value {{VALUE}} is not a string. This adapter could convert it to string, but it is disabled because 'nonStringBehaviour' setting is set to ABORT.")
                        .parameter("VALUE", getExcerpt(value), "An excerpt of the input value.")
                        .mitigation("Set 'nonStringBehaviour' to CONVERT_OR_ABORT or CONVERT_OR_NULL.")
                        .mitigation("Change your input data to strings.").mitigation("Use a different mapping.")
                        .toString(), this.column);
            } else {
                return null;
            }
        }
    }
}
