package com.exasol.adapter.document.documentnode.holder;

import java.util.Map;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.DocumentObject;

/**
 * Implementation of {@link DocumentObject} that simply holds the object properties as a map.
 */
public class ObjectHolderNode implements DocumentObject {
    private static final long serialVersionUID = -8862311988922376399L;
    /** @serial */
    private final Map<String, DocumentNode> value;

    /**
     * Create a new instance of {@link ObjectHolderNode}.
     * 
     * @param value map with the property mapping to wrap
     */
    public ObjectHolderNode(final Map<String, DocumentNode> value) {
        this.value = value;
    }

    @Override
    public Map<String, DocumentNode> getKeyValueMap() {
        return this.value;
    }

    @Override
    public DocumentNode get(final String key) {
        return this.value.get(key);
    }

    @Override
    public boolean hasKey(final String key) {
        return this.value.containsKey(key);
    }
}
