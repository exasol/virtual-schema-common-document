package com.exasol.adapter.document.documentnode;

import java.util.List;

/**
 * Interface for array / list document nodes.
 */
@java.lang.SuppressWarnings("squid:S119") // VisitorType does not fit naming conventions.
public interface DocumentArray extends DocumentNode {

    /**
     * Returns a list with document nodes wrapping the values of the list wrapped in this node.
     * 
     * @return list of document nodes.
     */
    @SuppressWarnings("java:S1452") // Use of wildcard is ok in this case as loss of type information is
                                    // acceptable.
    List<? extends DocumentNode> getValuesList();

    /**
     * Get a document node for an specific element of the wrapped array.
     * 
     * @param index index of the element that shall be returned
     * @return Document node wrapping the value.
     */
    DocumentNode getValue(int index);

    /**
     * Get the size of the wrapped array.
     * 
     * @return size of the wrapped array
     */
    int size();

    @Override
    default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }
}
