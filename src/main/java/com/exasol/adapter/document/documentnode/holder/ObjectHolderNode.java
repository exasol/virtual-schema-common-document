package com.exasol.adapter.document.documentnode.holder;

import java.util.Map;
import java.util.Objects;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.DocumentObject;

/**
 * Implementation of {@link DocumentObject} that simply holds the object properties as a map.
 */
public final class ObjectHolderNode implements DocumentObject {
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

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjectHolderNode other = (ObjectHolderNode) obj;
        return Objects.equals(value, other.value);
    }
}
