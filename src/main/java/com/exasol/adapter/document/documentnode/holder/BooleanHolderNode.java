package com.exasol.adapter.document.documentnode.holder;

import java.util.Objects;

import com.exasol.adapter.document.documentnode.DocumentBooleanValue;

/**
 * Implementation of {@link DocumentBooleanValue} that simply holds the boolean value in a variable.
 */
public final class BooleanHolderNode implements DocumentBooleanValue {
    private final boolean booleanValue;

    /**
     * Create an instance of {@link BooleanHolderNode}.
     * 
     * @param booleanValue boolean value to wrap
     */
    public BooleanHolderNode(final boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    @Override
    public boolean getValue() {
        return this.booleanValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(booleanValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BooleanHolderNode other = (BooleanHolderNode) obj;
        return booleanValue == other.booleanValue;
    }
}
