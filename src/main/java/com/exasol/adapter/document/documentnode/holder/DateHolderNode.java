package com.exasol.adapter.document.documentnode.holder;

import java.sql.Date;
import java.util.Objects;

import com.exasol.adapter.document.documentnode.DocumentDateValue;

/**
 * Implementation of {@link DocumentDateValue} that simply holds the date value in a variable.
 */
public final class DateHolderNode implements DocumentDateValue {
    private final Date dateValue;

    /**
     * Create a new instance of {@link DateHolderNode}.
     * 
     * @param dateValue date value to wrap
     */
    public DateHolderNode(final Date dateValue) {
        this.dateValue = dateValue;
    }

    @Override
    public Date getValue() {
        return this.dateValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateValue);
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
        DateHolderNode other = (DateHolderNode) obj;
        return Objects.equals(dateValue, other.dateValue);
    }
}
