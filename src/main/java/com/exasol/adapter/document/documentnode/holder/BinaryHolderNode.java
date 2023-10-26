package com.exasol.adapter.document.documentnode.holder;

import java.util.Arrays;

import com.exasol.adapter.document.documentnode.DocumentBinaryValue;

/**
 * Implementation of {@link DocumentBinaryValue} that simply holds the data in a variable.
 */
public final class BinaryHolderNode implements DocumentBinaryValue {
    private final byte[] data;

    /**
     * Create an instance of {@link BinaryHolderNode}.
     * 
     * @param data binary data to wrap
     */
    public BinaryHolderNode(final byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getBinary() {
        return this.data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
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
        final BinaryHolderNode other = (BinaryHolderNode) obj;
        return Arrays.equals(data, other.data);
    }

}
