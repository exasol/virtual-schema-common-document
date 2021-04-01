package com.exasol.adapter.document.documentnode;

/**
 * Interface for null values.
 */
public interface DocumentNullValue extends DocumentNode {
    @Override
    public default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }
}
