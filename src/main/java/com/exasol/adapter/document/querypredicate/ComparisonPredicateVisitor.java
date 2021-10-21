package com.exasol.adapter.document.querypredicate;

/**
 * This is an interface for visitors for {@link ComparisonPredicate}s.
 */
public interface ComparisonPredicateVisitor {
    /**
     * Visit a {@link ColumnLiteralComparisonPredicate}.
     * 
     * @param columnLiteralComparisonPredicate {@link ColumnLiteralComparisonPredicate} to visit
     */
    void visit(final ColumnLiteralComparisonPredicate columnLiteralComparisonPredicate);
}
