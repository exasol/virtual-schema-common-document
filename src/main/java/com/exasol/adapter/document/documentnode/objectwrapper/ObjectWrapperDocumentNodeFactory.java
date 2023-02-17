package com.exasol.adapter.document.documentnode.objectwrapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.errorreporting.ExaError;

/**
 * Factory for {@link DocumentNode}s from java object structures.
 */
public class ObjectWrapperDocumentNodeFactory {
    private ObjectWrapperDocumentNodeFactory() {
        // empty on purpose
    }

    /**
     * Get a {@link DocumentNode} wrapping a given java object.
     *
     * @param object object to wrap
     * @return built {@link DocumentNode} tree
     * @throws UnsupportedOperationException if a unsupported java type was passed
     */
    @SuppressWarnings("unchecked")
    public static DocumentNode getNodeFor(final Object object) {
        if (object instanceof List) {
            return new ListWrapperNode((List<Object>) object);
        } else if (object instanceof Map) {
            return new MapWrapperNode((Map<String, Object>) object);
        } else if (object instanceof Double) {
            return new DoubleHolderNode((Double) object);
        } else if (object instanceof Float) {
            return new DoubleHolderNode((Float) object);
        } else if (object instanceof Number) {
            return new BigDecimalHolderNode(new BigDecimal(object.toString()));
        } else if (object instanceof String) {
            return new StringHolderNode((String) object);
        } else if (object instanceof Boolean) {
            return new BooleanHolderNode((Boolean) object);
        } else if (object instanceof java.sql.Date) {
            return new DateHolderNode((Date) object);
        } else if (object instanceof java.sql.Timestamp) {
            return new TimestampHolderNode((Timestamp) object);
        } else if (object == null) {
            return new NullHolderNode();
        } else {
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-73")
                    .message("Unsupported object type {{type}}.", object.getClass().getName()).ticketMitigation()
                    .toString());
        }
    }
}
