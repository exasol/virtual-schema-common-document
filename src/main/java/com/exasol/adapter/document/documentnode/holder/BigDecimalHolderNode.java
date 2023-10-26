package com.exasol.adapter.document.documentnode.holder;

import java.math.BigDecimal;
import java.util.Objects;

import com.exasol.adapter.document.documentnode.DocumentDecimalValue;

/**
 * Implementation of {@link DocumentDecimalValue} that simply holds the big decimal value in a variable.
 */
public final class BigDecimalHolderNode implements DocumentDecimalValue {
    private final BigDecimal numberValue;

    /**
     * Create a new instance of {@link BigDecimalHolderNode}.
     * 
     * @param bigDecimal big decimal to wrap
     */
    public BigDecimalHolderNode(final BigDecimal bigDecimal) {
        this.numberValue = bigDecimal;
    }

    @Override
    public BigDecimal getValue() {
        return this.numberValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberValue);
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
        BigDecimalHolderNode other = (BigDecimalHolderNode) obj;
        return Objects.equals(numberValue, other.numberValue);
    }
}
