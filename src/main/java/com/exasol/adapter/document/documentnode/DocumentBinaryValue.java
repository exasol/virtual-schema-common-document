package com.exasol.adapter.document.documentnode;

/**
 * Interface for binary values.
 */
public interface DocumentBinaryValue extends DocumentNode {

    /**
     * Get the binary data.
     * 
     * @return byte array
     */
    public byte[] getBinary();

    @Override
    public default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }
}
