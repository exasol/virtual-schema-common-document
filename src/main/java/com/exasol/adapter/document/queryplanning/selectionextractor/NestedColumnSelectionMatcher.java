package com.exasol.adapter.document.queryplanning.selectionextractor;

import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.PropertyToColumnMapping;
import com.exasol.adapter.document.querypredicate.ComparisonPredicate;

/**
 * This class matches the predicates that contain comparisons on column in lists.
 */
public class NestedColumnSelectionMatcher implements SelectionMatcher {
    @Override
    public boolean matchComparison(final ComparisonPredicate comparison) {
        return comparison.getComparedColumns().stream().anyMatch(this::isNestedColumn);
    }

    private boolean isNestedColumn(final ColumnMapping columnMapping) {
        if (columnMapping instanceof PropertyToColumnMapping) {
            final PropertyToColumnMapping propertyToColumnMapping = (PropertyToColumnMapping) columnMapping;
            return propertyToColumnMapping.getPathToSourceProperty().indexOfFirstArrayAllSegment() != -1;
        }
        return false;
    }
}
