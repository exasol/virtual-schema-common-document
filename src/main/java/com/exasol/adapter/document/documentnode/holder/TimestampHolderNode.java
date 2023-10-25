package com.exasol.adapter.document.documentnode.holder;

import java.sql.Timestamp;

import com.exasol.adapter.document.documentnode.DocumentTimestampValue;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link DocumentTimestampValue} that simply holds the timestamp value in a variable.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public final class TimestampHolderNode implements DocumentTimestampValue {
    private final Timestamp timestampValue;

    @Override
    public Timestamp getValue() {
        return this.timestampValue;
    }
}
