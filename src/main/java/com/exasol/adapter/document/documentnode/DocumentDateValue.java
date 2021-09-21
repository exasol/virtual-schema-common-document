package com.exasol.adapter.document.documentnode;

import java.sql.Date;

/**
 * Interface for date values.
 */
public interface DocumentDateValue extends DocumentNode {
    @Override
    public default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get the date value.
     * 
     * @return date value
     */
    public Date getValue();
}
