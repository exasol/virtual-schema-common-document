package com.exasol.adapter.document.documentnode.holder;

import java.util.Map;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.DocumentObject;

import lombok.EqualsAndHashCode;

/**
 * Implementation of {@link DocumentObject} that simply holds the object properties as a map.
 */
@EqualsAndHashCode
public class ObjectHolderNode implements DocumentObject {
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
