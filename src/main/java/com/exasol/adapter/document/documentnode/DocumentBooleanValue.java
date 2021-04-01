package com.exasol.adapter.document.documentnode;

/**
 * Interface for boolean values.
 */
public interface DocumentBooleanValue extends DocumentNode {
    @Override
    public default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get the boolean value.
     * 
     * @return boolean value
     */
    public boolean getValue();
}
