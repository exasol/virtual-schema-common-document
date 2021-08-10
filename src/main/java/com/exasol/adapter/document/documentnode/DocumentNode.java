package com.exasol.adapter.document.documentnode;

/**
 * Interface for accessing document data. It is used to abstract from the value representations of different document
 * databases.
 */
public interface DocumentNode {
    /**
     * Accepts a {@link DocumentNodeVisitor} visitor.
     * 
     * @param visitor visitor to accept
     */
    void accept(DocumentNodeVisitor visitor);
}
