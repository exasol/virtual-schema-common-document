package com.exasol.adapter.document.querypredicate;

import java.util.Objects;

/**
 * This class represents a comparison between two values.
 */
public abstract class AbstractComparisonPredicate implements ComparisonPredicate {
    private final Operator operator;

    /**
     * Create a new instance of {@link AbstractComparisonPredicate}.
     * 
     * @param operator comparison operator
     */
    public AbstractComparisonPredicate(final Operator operator) {
        this.operator = operator;
    }

    @Override
    public Operator getOperator() {
        return this.operator;
    }

    @Override
    public String toString() {
        switch (this.operator) {
        case EQUAL:
            return "=";
        case NOT_EQUAL:
            return "!=";
        case LESS:
            return "<";
        case LESS_EQUAL:
            return "<=";
        case GREATER:
            return ">";
        case GREATER_EQUAL:
            return ">=";
        case LIKE:
            return "LIKE";
        case NOT_LIKE:
            return "NOT LIKE";
        default:
            throw new UnsupportedOperationException();// All possible operators are implemented
        }
    }

    @Override
    public QueryPredicate simplify() {
        return this;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractComparisonPredicate)) {
            return false;
        }
        final AbstractComparisonPredicate that = (AbstractComparisonPredicate) other;
        return this.operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.operator);
    }

    /**
     * Possible comparision operators.
     */
    public enum Operator {
        NOT_EQUAL, EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL, LIKE, NOT_LIKE
    }
}
