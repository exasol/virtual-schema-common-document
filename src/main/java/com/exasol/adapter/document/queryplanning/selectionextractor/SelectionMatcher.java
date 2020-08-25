package com.exasol.adapter.document.queryplanning.selectionextractor;

import com.exasol.adapter.document.querypredicate.ComparisonPredicate;

/**
 * This is an interface for matchers that match comparisons in selections. It is used by the {@link SelectionExtractor}
 * for splitting up queries.
 */
public interface SelectionMatcher {

    /**
     * Separate the comparisons in two groups: matched and unmatched groups.
     *
     * @param comparison comparison to test
     * @return {@code true} if comparison should be included in the selectedSelection
     */
    boolean matchComparison(ComparisonPredicate comparison);
}
