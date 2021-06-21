package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentFloatingPointValue;

/**
 * Implementation of {@link DocumentFloatingPointValue} that simply holds the double value value in a variable.
 */
public class DoubleHolderNode implements DocumentFloatingPointValue {
    /** @serial */
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
}
