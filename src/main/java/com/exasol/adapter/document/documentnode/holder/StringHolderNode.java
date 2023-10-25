package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentStringValue;

import lombok.EqualsAndHashCode;

/**
 * Implementation of {@link DocumentStringValue} that simply holds the string value in a variable.
 */
@EqualsAndHashCode
public final class StringHolderNode implements DocumentStringValue {
    private final String stringValue;

    /**
     * Create a new {@link StringHolderNode}.
     * 
     * @param stringValue string to wrap.
     */
    public StringHolderNode(final String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String getValue() {
        return this.stringValue;
    }
}
