package com.exasol.adapter.document.documentnode.holder;

import java.util.Objects;

import com.exasol.adapter.document.documentnode.DocumentFloatingPointValue;

/**
 * Implementation of {@link DocumentFloatingPointValue} that simply holds the double value in a variable.
 */
public final class DoubleHolderNode implements DocumentFloatingPointValue {
    private final double value;

    /**
     * Create a new instance of {@link DoubleHolderNode}.
     * 
     * @param value double value to hold
     */
    public DoubleHolderNode(final double value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
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
        DoubleHolderNode other = (DoubleHolderNode) obj;
        return Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
    }
}
