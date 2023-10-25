package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentNullValue;

/**
 * Implementation of {@link DocumentNullValue}.
 */
public final class NullHolderNode implements DocumentNullValue {
    @Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof NullHolderNode);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
