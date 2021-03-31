package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentBooleanValue;

/**
 * Implementation of {@link DocumentBooleanValue} that simply holds the boolean value value in a variable.
 */
public class BooleanHolderNode implements DocumentBooleanValue {
    /** @serial */
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
}
