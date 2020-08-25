package com.exasol.adapter.document.queryplanning.selectionextractor;

import com.exasol.adapter.document.mapping.IterationIndexColumnMapping;
import com.exasol.adapter.document.querypredicate.ColumnLiteralComparisonPredicate;
import com.exasol.adapter.document.querypredicate.ComparisonPredicate;

/**
 * This class matches the predicates on {@link IterationIndexColumnMapping}s from a selection. Selections on these
 * predicates can't be pushed down as their value is determined during the schema mapping.
 */
public class IndexColumnSelectionMatcher implements SelectionMatcher {

    public boolean matchComparison(final ComparisonPredicate comparison) {
        if (comparison.getComparedColumns().stream()
                .anyMatch(column -> column instanceof IterationIndexColumnMapping)) {
            if (!(comparison instanceof ColumnLiteralComparisonPredicate)) {
                throw new UnsupportedOperationException(
                        "INDEX columns can only be compared to literals. Please change your SQL query.");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
