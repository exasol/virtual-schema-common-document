package com.exasol.adapter.document.documentnode.holder;

import com.exasol.adapter.document.documentnode.DocumentBinaryValue;

import lombok.EqualsAndHashCode;

/**
 * Implementation of {@link DocumentBinaryValue} that simply holds the data in a variable.
 */
@EqualsAndHashCode
public class BinaryHolderNode implements DocumentBinaryValue {
    private final byte[] data;

    public BinaryHolderNode(final byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getBinary() {
        return this.data;
    }
}
