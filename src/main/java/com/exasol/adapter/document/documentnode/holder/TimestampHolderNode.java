package com.exasol.adapter.document.documentnode.holder;

import java.sql.Timestamp;
import java.util.Objects;

import com.exasol.adapter.document.documentnode.DocumentTimestampValue;

/**
 * Implementation of {@link DocumentTimestampValue} that simply holds the timestamp value in a variable.
 */
public final class TimestampHolderNode implements DocumentTimestampValue {
    private final Timestamp timestampValue;

    /**
     * Create a new instance of {@link TimestampHolderNode}.
     * 
     * @param value timestamp to wrap
     */
    public TimestampHolderNode(final Timestamp value) {
        this.timestampValue = value;
    }

    @Override
    public Timestamp getValue() {
        return this.timestampValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestampValue);
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
        final TimestampHolderNode other = (TimestampHolderNode) obj;
        return Objects.equals(timestampValue, other.timestampValue);
    }
}
