package com.exasol.adapter.document.documentnode;

/**
 * Interface for null values
 */
public interface DocumentStringValue extends DocumentNode {
    @Override
    public default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get the string value.
     * 
     * @return string value
     */
    public String getValue();
}
