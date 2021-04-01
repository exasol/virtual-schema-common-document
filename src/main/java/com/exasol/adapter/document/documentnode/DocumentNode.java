package com.exasol.adapter.document.documentnode;

import java.io.Serializable;

/**
 * Interface for accessing document data. It is used to abstract from the value representations of different document
 * databases.
 */
public interface DocumentNode extends Serializable {
    /**
     * Accepts a {@link DocumentNodeVisitor} visitor.
     * 
     * @param visitor visitor to accept
     */
    void accept(DocumentNodeVisitor visitor);
}
