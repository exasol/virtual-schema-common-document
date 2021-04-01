package com.exasol.adapter.document.documentnode.holder;

import java.math.BigDecimal;

import com.exasol.adapter.document.documentnode.DocumentBigDecimalValue;

/**
 * Implementation of {@link DocumentBigDecimalValue} that simply holds the big decimal value in a variable.
 */
public class BigDecimalHolderNode implements DocumentBigDecimalValue {
    /** @serial */
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
}
