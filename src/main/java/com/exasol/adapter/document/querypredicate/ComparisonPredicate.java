package com.exasol.adapter.document.querypredicate;

import java.util.List;

import com.exasol.adapter.document.mapping.ColumnMapping;

/**
 * This interface represents a comparison between a literal and a column of a table.
 */
public interface ComparisonPredicate extends QueryPredicate {

    /**
     * Get the comparison operator.
     *
     * @return the operator
     */
    AbstractComparisonPredicate.Operator getOperator();

    /**
     * Accept an {@link ComparisonPredicateVisitor}.
     * 
     * @param visitor to accept
     */
    void accept(ComparisonPredicateVisitor visitor);

    @Override
    default void accept(final QueryPredicateVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Get a list of {@link ColumnMapping}s involved in the comparison.
     * 
     * @return list of {@link ColumnMapping}s involved in the comparison
     */
    List<ColumnMapping> getComparedColumns();

    /**
     * Negates this operator. e.g. {@code A = B --> A != B}
     * 
     * @return negated operator
     */
    ComparisonPredicate negate();
}
