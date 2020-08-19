package com.exasol.adapter.document.documentnode;

import java.util.Map;

/**
 * Interface for object document nodes.
 */
@java.lang.SuppressWarnings("squid:S119") // VisitorType does not fit naming conventions.
public interface DocumentObject<VisitorType> extends DocumentNode<VisitorType> {

    /**
     * Get a map that represents this object. The values are wrapped as document nodes.
     * 
     * @return map representing this object
     */
    Map<String, DocumentNode<VisitorType>> getKeyValueMap();

    /**
     * Returns a specific object value of given key.
     * 
     * @param key The key that shall be accessed
     * @return result wrapped in a document node.
     */
    DocumentNode<VisitorType> get(String key);

    /**
     * Checks if this object contains a given key.
     * 
     * @param key key to check
     * @return {@code true} if key is present
     */
    boolean hasKey(String key);
}
