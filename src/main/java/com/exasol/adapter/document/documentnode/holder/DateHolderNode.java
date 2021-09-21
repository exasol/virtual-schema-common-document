package com.exasol.adapter.document.documentnode.holder;

import java.sql.Date;

import com.exasol.adapter.document.documentnode.DocumentDateValue;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link DocumentDateValue} that simply holds the date value in a variable.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class DateHolderNode implements DocumentDateValue {
    private final Date dateValue;

    @Override
    public Date getValue() {
        return this.dateValue;
    }
}
