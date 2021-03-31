package com.exasol.adapter.document.documentnode;

import java.math.BigDecimal;

/**
 * Interface for BigDecimal values.
 */
public interface DocumentBigDecimalValue extends DocumentNode {
    @Override
    public default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get the BigDecimal value.
     * 
     * @return BigDecimal value
     */
    public BigDecimal getValue();
}
