package com.exasol.adapter.document.documentnode;

/**
 * Interface for BigDecimal values.
 */
public interface DocumentFloatingPointValue extends DocumentNode {
    @Override
    public default void accept(final DocumentNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get the floating-point value.
     * 
     * @return floating-point value
     */
    public double getValue();
}
