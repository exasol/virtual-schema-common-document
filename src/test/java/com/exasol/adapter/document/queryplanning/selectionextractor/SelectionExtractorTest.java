package com.exasol.adapter.document.queryplanning.selectionextractor;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.getColumnMappingExample;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.IterationIndexColumnMapping;
import com.exasol.adapter.document.querypredicate.*;
import com.exasol.adapter.sql.SqlLiteralString;

class SelectionExtractorTest {

    private static final QueryPredicate NON_INDEX_COMPARISON = buildNonIndexComparison("column1");
    private static final QueryPredicate NON_INDEX_COMPARISON_2 = buildNonIndexComparison("column2");
    private static final QueryPredicate INDEX_COMPARISON = buildIndexComparison("indexColumn1");
    private static final SelectionExtractor EXTRACTOR = new SelectionExtractor(new IndexColumnSelectionMatcher());

    private static ColumnLiteralComparisonPredicate buildNonIndexComparison(final String columnName) {
        final ColumnMapping column = getColumnMappingExample().exasolColumnName(columnName).build();
        final SqlLiteralString literal = new SqlLiteralString("valueToCompareTo");
        return new ColumnLiteralComparisonPredicate(AbstractComparisonPredicate.Operator.EQUAL, column, literal);
    }

    private static ColumnLiteralComparisonPredicate buildIndexComparison(final String columnName) {
        final IterationIndexColumnMapping column = new IterationIndexColumnMapping(columnName,
                DocumentPathExpression.builder().addObjectLookup(columnName).addArrayAll().build());
        final SqlLiteralString literal = new SqlLiteralString("valueToCompareTo");
        return new ColumnLiteralComparisonPredicate(AbstractComparisonPredicate.Operator.EQUAL, column, literal);
    }

    @Test
    void testExtractWithNoIndexColumn() {
        final SelectionExtractor.Result result = EXTRACTOR.extractIndexColumnSelection(NON_INDEX_COMPARISON);
        assertAll(() -> assertThat(result.getSelectedSelection().asQueryPredicate(), equalTo(new NoPredicate())),
                () -> assertThat(result.getRemainingSelection().asQueryPredicate(), equalTo(NON_INDEX_COMPARISON)));
    }

    @Test
    void testExtractOnlyIndexColumn() {
        final SelectionExtractor.Result result = EXTRACTOR.extractIndexColumnSelection(INDEX_COMPARISON);
        assertAll(() -> assertThat(result.getSelectedSelection().asQueryPredicate(), equalTo(INDEX_COMPARISON)),
                () -> assertThat(result.getRemainingSelection().asQueryPredicate(), equalTo(new NoPredicate())));
    }

    @Test
    void testExtractIndexAndNonIndexColumnFromAnd() {
        final SelectionExtractor.Result result = EXTRACTOR.extractIndexColumnSelection(
                new LogicalOperator(Set.of(INDEX_COMPARISON, NON_INDEX_COMPARISON), LogicalOperator.Operator.AND));
        assertAll(() -> assertThat(result.getSelectedSelection().asQueryPredicate(), equalTo(INDEX_COMPARISON)),
                () -> assertThat(result.getRemainingSelection().asQueryPredicate(), equalTo(NON_INDEX_COMPARISON)));
    }

    @Test
    void testExtractIndexAndNonIndexColumnFromOr() {
        final LogicalOperator predicate = new LogicalOperator(Set.of(INDEX_COMPARISON, NON_INDEX_COMPARISON),
                LogicalOperator.Operator.OR);
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> EXTRACTOR.extractIndexColumnSelection(predicate));
        assertThat(exception.getMessage(), equalTo(
                "This query combines selections on columns in a way, so that the selection can't be split up."));
    }

    @Test
    void testExtractFromAndAndOr() {
        final SelectionExtractor.Result result = EXTRACTOR
                .extractIndexColumnSelection(new LogicalOperator(
                        Set.of(INDEX_COMPARISON, new LogicalOperator(
                                Set.of(NON_INDEX_COMPARISON, NON_INDEX_COMPARISON_2), LogicalOperator.Operator.OR)),
                        LogicalOperator.Operator.AND));
        assertAll(() -> assertThat(result.getSelectedSelection().asQueryPredicate(), equalTo(INDEX_COMPARISON)),
                () -> assertThat(result.getRemainingSelection().asQueryPredicate(),
                        equalTo(new LogicalOperator(Set.of(NON_INDEX_COMPARISON_2, NON_INDEX_COMPARISON),
                                LogicalOperator.Operator.OR))));
    }
}