package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.ExcerptGenerator.getExcerpt;
import static com.exasol.adapter.document.mapping.TruncateableMappingErrorBehaviour.NULL;
import static com.exasol.adapter.document.mapping.TruncateableMappingErrorBehaviour.TRUNCATE;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.NullLiteral;
import com.exasol.sql.expression.literal.StringLiteral;

/**
 * ValueMapper for {@link PropertyToVarcharColumnMapping}
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
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
    protected final ValueExpression mapValue(final DocumentNode documentValue) {
        final ConversionResult result = mapStringValue(documentValue);
        final String stringResult = handleResult(result);
        if (stringResult == null) {
            return NullLiteral.nullLiteral();
        } else {
            return StringLiteral.of(stringResult);
        }
    }

    private String handleResult(final ConversionResult result) {
        if (result instanceof MappedStringResult) {
            return handleConvertedResult((MappedStringResult) result);
        } else {
            return handleNotConvertedResult((FailedConversionResult) result);
        }
    }

    private String handleConvertedResult(final MappedStringResult stringValue) {
        if (stringValue.isConverted()
                && this.column.getNonStringBehaviour().equals(ConvertableMappingErrorBehaviour.ABORT)) {
            throw new ColumnValueExtractorException(ExaError.messageBuilder("E-VSD-36").message(
                    "The input value {{VALUE}} is not a string. This adapter could convert it to string, but it is disabled because 'nonStringBehaviour' setting is set to ABORT.")
                    .parameter("VALUE", getExcerpt(stringValue.getValue()), "An excerpt of the input value.")
                    .mitigation("Set 'nonStringBehaviour' to CONVERT_OR_ABORT or CONVERT_OR_NULL.")
                    .mitigation("Change your input data to strings.").toString(), this.column);
        } else if (stringValue.isConverted()
                && this.column.getNonStringBehaviour().equals(ConvertableMappingErrorBehaviour.NULL)) {
            return null;
        } else {
            return handleOverflowIfNecessary(stringValue.getValue());
        }
    }

    private String handleNotConvertedResult(final FailedConversionResult result) {
        if (this.column.getNonStringBehaviour() == ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT
                || this.column.getNonStringBehaviour() == ConvertableMappingErrorBehaviour.ABORT) {
            throw new ColumnValueExtractorException(ExaError.messageBuilder("E-VSD-37")
                    .message("An input value of type {{TYPE}} for column {{COLUMN}} could not be converted to string.")
                    .parameter("TYPE", result.getTypeName())//
                    .parameter("COLUMN", this.column.getExasolColumnName())
                    .mitigation("Change the value in your input data")
                    .mitigation("Change the nonStringBehaviour of the column to NULL or CONVERT_OR_NULL.").toString(),
                    this.column);
        } else {
            return null;
        }
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

    private ConversionResult mapStringValue(final DocumentNode property) {
        final ToStringVisitor toStringVisitor = new ToStringVisitor();
        property.accept(toStringVisitor);
        return toStringVisitor.getResult();
    }

    /**
     * This interface is used to pass the result of {@link #mapStringValue(DocumentNode)} from the concrete
     * implementation to this abstract class.
     *
     * @implNote public so that it is accessible from test code
     */
    private interface ConversionResult {

    }

    private static class MappedStringResult implements ConversionResult {
        private final String value;
        private final boolean isConverted;

        public MappedStringResult(final String value, final boolean isConverted) {
            this.value = value;
            this.isConverted = isConverted;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isConverted() {
            return this.isConverted;
        }
    }

    private static class FailedConversionResult implements ConversionResult {
        private final String typeName;

        /**
         * Create a new instance of {@link FailedConversionResult}.
         *
         * @param typeName name of the unsupported type
         */
        public FailedConversionResult(final String typeName) {
            this.typeName = typeName;
        }

        /**
         * Get the name of the unsupported type.
         *
         * @return name of the unsupported type
         */
        public String getTypeName() {
            return this.typeName;
        }
    }

    private static class ToStringVisitor implements DocumentNodeVisitor {
        private ConversionResult result;

        @Override
        public void visit(final DocumentObject objectNode) {
            this.result = new FailedConversionResult("object");
        }

        @Override
        public void visit(final DocumentArray arrayNode) {
            this.result = new FailedConversionResult("array");
        }

        @Override
        public void visit(final DocumentStringValue stringNode) {
            this.result = new MappedStringResult(stringNode.getValue(), false);
        }

        @Override
        public void visit(final DocumentDecimalValue numberNode) {
            this.result = new MappedStringResult(numberNode.getValue().toString(), true);
        }

        @Override
        public void visit(final DocumentNullValue nullNode) {
            this.result = new MappedStringResult(null, false);
        }

        @Override
        public void visit(final DocumentBooleanValue booleanNode) {
            this.result = new MappedStringResult(booleanNode.getValue() ? "true" : "false", true);
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            this.result = new MappedStringResult(String.valueOf(floatingPointValue.getValue()), true);
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            final String bas64Encoded = new String(Base64.getEncoder().encode(binaryValue.getBinary()),
                    StandardCharsets.UTF_8);
            this.result = new MappedStringResult(bas64Encoded, true);
        }

        public ConversionResult getResult() {
            return this.result;
        }
    }
}
