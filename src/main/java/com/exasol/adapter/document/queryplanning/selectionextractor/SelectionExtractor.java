package com.exasol.adapter.document.queryplanning.selectionextractor;

import java.util.*;
import java.util.stream.Collectors;

import com.exasol.adapter.document.mapping.IterationIndexColumnMapping;
import com.exasol.adapter.document.querypredicate.QueryPredicate;
import com.exasol.adapter.document.querypredicate.normalizer.DnfAnd;
import com.exasol.adapter.document.querypredicate.normalizer.DnfComparison;
import com.exasol.adapter.document.querypredicate.normalizer.DnfNormalizer;
import com.exasol.adapter.document.querypredicate.normalizer.DnfOr;
import com.exasol.errorreporting.ExaError;

/**
 * This class can split up a selection into two selections that can be combined with an AND. The decision which
 * comparison belongs to the {@link SelectionMatcher}. If multiple matchers are passed, the comparison is extracted if
 * one or more matchers match the comparison.
 */
public class SelectionExtractor {
    private final DnfNormalizer dnfNormalizer;
    private final List<SelectionMatcher> matchers;

    /**
     * Create an instance of {@link SelectionExtractor}
     *
     * @param matchers on or more matcher that matches the comparison to be extracted
     */
    public SelectionExtractor(final SelectionMatcher... matchers) {
        this.dnfNormalizer = new DnfNormalizer();
        this.matchers = Arrays.asList(matchers);
    }

    /**
     * Extract the predicates on {@link IterationIndexColumnMapping}s from a selection.
     *
     * @param selection to split
     * @return {@link Result} containing one selection with the predicates on {@link IterationIndexColumnMapping}s and
     *         another on the other columns.
     */
    public Result extractIndexColumnSelection(final QueryPredicate selection) {
        final DnfOr dnfOr = this.dnfNormalizer.normalize(selection);
        final List<SplitDnfAnd> splitDnfAnds = dnfOr.getOperands().stream().map(this::splitUpDnfAnd)
                .collect(Collectors.toList());
        if (splitDnfAnds.isEmpty()) {
            return new Result(new DnfOr(Collections.emptySet()), new DnfOr(Collections.emptySet()));
        }
        final Set<Set<DnfComparison>> indexComparisonDnfAnds = splitDnfAnds.stream()
                .map(splitDnfAnd -> splitDnfAnd.matchedComparisons).collect(Collectors.toSet());
        final Set<Set<DnfComparison>> nonIndexComparisonAnds = splitDnfAnds.stream()
                .map(splitDnfAnd -> splitDnfAnd.notMatchedComparisons).collect(Collectors.toSet());
        if (indexComparisonDnfAnds.size() == 1 || nonIndexComparisonAnds.size() == 1) {// All ANDs have the same index
                                                                                       // predicates
            final DnfOr indexSelection = wrapInDnfOr(indexComparisonDnfAnds);
            final DnfOr nonIndexSelection = wrapInDnfOr(nonIndexComparisonAnds);
            return new Result(indexSelection, nonIndexSelection);
        } else {
            throw new UnsupportedOperationException(
                    ExaError.messageBuilder("E-VSD-59").message(
                            "This query combines selections on columns in a way, so that the selection can't be split up.")
                            .mitigation(
                                    "Change your query: Try to simplify AND and OR constructs or move parts of the selection in a wrapping SELECT statement).")
                            .toString());
        }
    }

    private DnfOr wrapInDnfOr(final Set<Set<DnfComparison>> nonIndexComparisonAnds) {
        return new DnfOr(nonIndexComparisonAnds.stream().map(DnfAnd::new).collect(Collectors.toSet()));
    }

    private SplitDnfAnd splitUpDnfAnd(final DnfAnd dnfAnd) {
        final SplitDnfAnd result = new SplitDnfAnd();
        for (final DnfComparison comparison : dnfAnd.getOperands()) {
            if (anyMatchesComparison(comparison)) {
                result.matchedComparisons.add(comparison);
            } else {
                result.notMatchedComparisons.add(comparison);
            }
        }
        return result;
    }

    private boolean anyMatchesComparison(final DnfComparison comparison) {
        return this.matchers.stream()
                .anyMatch(eachMatcher -> eachMatcher.matchComparison(comparison.getComparisonPredicate()));
    }

    /**
     * This class stores the result of {@link #extractIndexColumnSelection(QueryPredicate)}.
     */
    public static class Result {
        private final DnfOr selectedSelection;
        private final DnfOr remainingSelection;

        public Result(final DnfOr selectedSelection, final DnfOr remainingSelection) {
            this.selectedSelection = selectedSelection;
            this.remainingSelection = remainingSelection;
        }

        /**
         * Get the selection with the predicates that were matched.
         * 
         * @return DNF normalized predicate structure
         */
        public DnfOr getSelectedSelection() {
            return this.selectedSelection;
        }

        /**
         * Get the selection with the predicates that were not matched.
         *
         * @return DNF normalized predicate structure
         */
        public DnfOr getRemainingSelection() {
            return this.remainingSelection;
        }
    }

    private static class SplitDnfAnd {
        final Set<DnfComparison> notMatchedComparisons = new HashSet<>();
        final Set<DnfComparison> matchedComparisons = new HashSet<>();
    }
}
