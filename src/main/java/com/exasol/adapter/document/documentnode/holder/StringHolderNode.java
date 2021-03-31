package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentStringValue;

/**
 * Implementation of {@link DocumentStringValue} that simply holds the string value in a variable.
 */
public class StringHolderNode implements DocumentStringValue {
    private static final long serialVersionUID = -6356072467161635811L;
    /** @serial */
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
