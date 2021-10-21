package com.exasol.adapter.document.querypredicate;

/**
 * Visitor for {@link QueryPredicate}.
 */
public interface QueryPredicateVisitor {

    /**
     * Visit {@link ComparisonPredicate}.
     *
     * @param comparisonPredicate {@link ComparisonPredicate} to visit
     */
    void visit(ComparisonPredicate comparisonPredicate);

    /**
     * Visit {@link LogicalOperator}.
     *
     * @param logicalOperator {@link LogicalOperator} to visit
     */
    void visit(LogicalOperator logicalOperator);

    /**
     * Visit {@link NoPredicate}.
     *
     * @param noPredicate {@link NoPredicate} to visit
     */
    void visit(NoPredicate noPredicate);

    /**
     * Visit {@link NotPredicate}.
     *
     * @param notPredicate {@link NotPredicate} to visit
     */
    void visit(NotPredicate notPredicate);
}
