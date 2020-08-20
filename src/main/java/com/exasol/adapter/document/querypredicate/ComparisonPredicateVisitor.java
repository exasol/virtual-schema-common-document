package com.exasol.adapter.document.querypredicate;

/**
 * This is an interface for visitors for {@link ComparisonPredicate}s.
 */
public interface ComparisonPredicateVisitor {
    void visit(final ColumnLiteralComparisonPredicate columnLiteralComparisonPredicate);
}
