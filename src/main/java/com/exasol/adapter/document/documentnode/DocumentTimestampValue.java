package com.exasol.adapter.document.documentnode;

import java.sql.Timestamp;

/**
 * Interface for timestamp values.
 */
public interface DocumentTimestampValue extends DocumentNode {
    @Override
    public default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get the date value.
     * 
     * @return date value
     */
    public Timestamp getValue();
}
