package com.exasol.adapter.document.mapping;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Map;

import javax.json.*;
import javax.json.spi.JsonProvider;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.errorreporting.ExaError;

/**
 * {@link ColumnValueExtractor} for {@link PropertyToJsonColumnMapping}.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class PropertyToJsonColumnValueExtractor extends AbstractPropertyToColumnValueExtractor {
    private final PropertyToJsonColumnMapping column;
    private static final JsonProvider JSON = JsonProvider.provider();

    /**
     * Create an instance of {@link PropertyToJsonColumnValueExtractor}.
     * 
     * @param column {@link PropertyToJsonColumnMapping}
     */
    public PropertyToJsonColumnValueExtractor(final PropertyToJsonColumnMapping column) {
        super(column);
        this.column = column;
    }

    @Override
    protected final Object mapValue(final DocumentNode documentValue) {
        final JsonValue jsonResult = ToJsonVisitor.convert(documentValue);
        if (jsonResult == JsonValue.NULL) {
            return null;
        } else {
            return handleOverflowIfRequired(jsonResult);
        }
    }

    private Object handleOverflowIfRequired(final JsonValue jsonResult) {
        final String jsonString = jsonResult.toString();
        if (jsonString.length() > this.column.getVarcharColumnSize()) {
            return handleOverflow();
        } else {
            return jsonString;
        }
    }

    private Object handleOverflow() {
        if (this.column.getOverflowBehaviour().equals(MappingErrorBehaviour.ABORT)) {
            throw new OverflowException(ExaError.messageBuilder("E-VSD-35")
                    .message("A generated JSON did exceed the configured maximum size of the column {{COLUMN_NAME}}.")
                    .parameter("COLUMN_NAME", this.column.getExasolColumnName())
                    .mitigation("Increase the 'varcharColumnSize' in your mapping definition.")
                    .mitigation("Set the 'overflowBehaviour' to 'NULL'.").toString(), this.column);
        } else {
            return null;
        }
    }

    private static class ToJsonVisitor implements DocumentNodeVisitor {
        private JsonValue jsonValue;

        private static JsonValue convert(final DocumentNode node) {
            final ToJsonVisitor toJsonVisitor = new ToJsonVisitor();
            node.accept(toJsonVisitor);
            return toJsonVisitor.getJsonValue();
        }

        @Override
        public void visit(final DocumentObject objectNode) {
            final JsonObjectBuilder jsonObjectBuilder = JSON.createObjectBuilder();
            for (final Map.Entry<String, DocumentNode> entry : objectNode.getKeyValueMap().entrySet()) {
                jsonObjectBuilder.add(entry.getKey(), ToJsonVisitor.convert(entry.getValue()));
            }
            this.jsonValue = jsonObjectBuilder.build();
        }

        @Override
        public void visit(final DocumentArray arrayNode) {
            final JsonArrayBuilder jsonArrayBuilder = JSON.createArrayBuilder();
            for (final DocumentNode node : arrayNode.getValuesList()) {
                jsonArrayBuilder.add(ToJsonVisitor.convert(node));
            }
            this.jsonValue = jsonArrayBuilder.build();
        }

        @Override
        public void visit(final DocumentStringValue stringNode) {
            this.jsonValue = JSON.createValue(stringNode.getValue());
        }

        @Override
        public void visit(final DocumentDecimalValue numberNode) {
            this.jsonValue = JSON.createValue(numberNode.getValue());
        }

        @Override
        public void visit(final DocumentNullValue nullNode) {
            this.jsonValue = JsonValue.NULL;
        }

        @Override
        public void visit(final DocumentBooleanValue booleanNode) {
            this.jsonValue = booleanNode.getValue() ? JsonValue.TRUE : JsonValue.FALSE;
        }

        @Override
        public void visit(final DocumentFloatingPointValue floatingPointValue) {
            this.jsonValue = JSON.createValue(floatingPointValue.getValue());
        }

        @Override
        public void visit(final DocumentBinaryValue binaryValue) {
            this.jsonValue = JSON.createValue(
                    new String(Base64.getEncoder().encode(binaryValue.getBinary()), StandardCharsets.UTF_8));
        }

        @Override
        public void visit(final DocumentDateValue dateValue) {
            this.jsonValue = JSON.createValue(dateValue.getValue().toString());
        }

        @Override
        public void visit(final DocumentTimestampValue timestampValue) {
            final Timestamp timestamp = timestampValue.getValue();
            this.jsonValue = JSON.createValue(timestamp.toInstant().toString());
        }

        public JsonValue getJsonValue() {
            return this.jsonValue;
        }
    }
}
