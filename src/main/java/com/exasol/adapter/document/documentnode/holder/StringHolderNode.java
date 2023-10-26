package com.exasol.adapter.document.documentnode.holder;

import java.util.Objects;

import com.exasol.adapter.document.documentnode.DocumentStringValue;

/**
 * Implementation of {@link DocumentStringValue} that simply holds the string value in a variable.
 */
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

    @Override
    public int hashCode() {
        return Objects.hash(stringValue);
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
        final StringHolderNode other = (StringHolderNode) obj;
        return Objects.equals(stringValue, other.stringValue);
    }
}
