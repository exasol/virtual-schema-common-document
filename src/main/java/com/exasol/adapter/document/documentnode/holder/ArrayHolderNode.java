package com.exasol.adapter.document.documentnode.holder;

import java.util.List;
import java.util.Objects;

import com.exasol.adapter.document.documentnode.DocumentArray;
import com.exasol.adapter.document.documentnode.DocumentNode;

/**
 * Implementation of {@link DocumentArray} that simply holds the list elements as array.
 */
public final class ArrayHolderNode implements DocumentArray {
    private final List<DocumentNode> value;

    /**
     * Create a new instance of {@link ArrayHolderNode}.
     * 
     * @param value list of children
     */
    public ArrayHolderNode(final List<DocumentNode> value) {
        this.value = value;
    }

    @Override
    public List<DocumentNode> getValuesList() {
        return this.value;
    }

    @Override
    public DocumentNode getValue(final int index) {
        return this.value.get(index);
    }

    @Override
    public int size() {
        return this.value.size();
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
        final ArrayHolderNode other = (ArrayHolderNode) obj;
        return Objects.equals(value, other.value);
    }
}
