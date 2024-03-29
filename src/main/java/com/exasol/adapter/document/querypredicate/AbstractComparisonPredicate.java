package com.exasol.adapter.document.querypredicate;

import java.util.Objects;

import com.exasol.errorreporting.ExaError;

/**
 * This class represents a comparison between two values.
 */
public abstract class AbstractComparisonPredicate implements ComparisonPredicate {
    /** Escape character used in LIKE predicates. */
    public static final String LIKE_ESCAPE_CHAR = "\\";
    private final Operator operator;

    /**
     * Create a new instance of {@link AbstractComparisonPredicate}.
     * 
     * @param operator comparison operator
     */
    protected AbstractComparisonPredicate(final Operator operator) {
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
            throw new UnsupportedOperationException(
                    ExaError.messageBuilder("F-VSD-6").message("Unimplemented operator {{OPERATOR}}.")
                            .parameter("OPERATOR", this.operator).ticketMitigation().toString());// All possible
                                                                                                 // operators
        // are implemented
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
        /** Not equals operator */
        NOT_EQUAL,
        /** Equals operator */
        EQUAL,
        /** Less operator */
        LESS,
        /** Less or equal operator */
        LESS_EQUAL,
        /** Greater operator */
        GREATER,
        /** Greater or equals operator */
        GREATER_EQUAL,
        /** Like operator */
        LIKE,
        /** Not like operator */
        NOT_LIKE
    }
}
