package com.exasol.adapter.document.documentnode;

import java.io.Serializable;

/**
 * This is a simple interface for accessing document data. It is used to abstract from the value representations of
 * different document databases. It accepts a generic visitor. See /doc/diagrams/documentnode.puml.
 */

@java.lang.SuppressWarnings("squid:S119") // VisitorType does not fit naming conventions.
public interface DocumentNode<VisitorType> extends Serializable {
    /**
     * Accepts a VisitorType visitor.
     * 
     * @param visitor generic visitor to accept
     */
    void accept(VisitorType visitor);
}
