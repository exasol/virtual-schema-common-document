package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentNullValue;

/**
 * Implementation of {@link DocumentNullValue}.
 */
public final class NullHolderNode implements DocumentNullValue {
    /** Creates a node representing a document null value. */
    public NullHolderNode() {
        // Intentionally empty to enable Javadoc.
    }

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
