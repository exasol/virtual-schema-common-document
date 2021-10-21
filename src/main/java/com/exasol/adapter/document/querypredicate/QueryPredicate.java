package com.exasol.adapter.document.querypredicate;

/**
 * This interface represents a selection predicate. Using the classes implementing this interface a where clause is
 * modeled.
 */
public interface QueryPredicate {
    /**
     * Accept a {@link QueryPredicateVisitor}.
     * 
     * @param visitor visitor to accept
     */
    void accept(QueryPredicateVisitor visitor);

    /**
     * Eliminate ANDs and ORs with no or only one operand.
     * 
     * @return simplified predicate.
     */
    QueryPredicate simplify();
}
