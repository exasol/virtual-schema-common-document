package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentBooleanValue;

import lombok.EqualsAndHashCode;

/**
 * Implementation of {@link DocumentBooleanValue} that simply holds the boolean value in a variable.
 */
@EqualsAndHashCode
public class BooleanHolderNode implements DocumentBooleanValue {
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
