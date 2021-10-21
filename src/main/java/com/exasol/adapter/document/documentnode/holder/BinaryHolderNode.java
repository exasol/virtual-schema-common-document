package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentBinaryValue;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link DocumentBinaryValue} that simply holds the data in a variable.
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public class BinaryHolderNode implements DocumentBinaryValue {
    private final byte[] data;

    @Override
    public byte[] getBinary() {
        return this.data;
    }
}
