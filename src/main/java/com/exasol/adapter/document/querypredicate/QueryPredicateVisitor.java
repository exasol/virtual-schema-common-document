package com.exasol.adapter.document.querypredicate;

/**
 * Visitor for {@link QueryPredicate}.
 */
public interface QueryPredicateVisitor {
    void visit(ComparisonPredicate comparisonPredicate);

    void visit(LogicalOperator logicalOperator);

    void visit(NoPredicate noPredicate);

    void visit(NotPredicate notPredicate);
}
