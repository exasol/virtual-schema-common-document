package com.exasol.adapter.document.queryplanning.selectionextractor;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.getColumnMappingExample;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.IterationIndexColumnMapping;
import com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate;
import com.exasol.adapter.document.querypredicate.ColumnLiteralComparisonPredicate;
import com.exasol.adapter.sql.SqlLiteralString;

class IndexColumnSelectionMatcherTest {

    @Test
    void testMatch() {
        final IterationIndexColumnMapping column = new IterationIndexColumnMapping("INDEX",
                DocumentPathExpression.builder().addObjectLookup("test").addArrayAll().build());
        final SqlLiteralString literal = new SqlLiteralString("valueToCompareTo");
        final ColumnLiteralComparisonPredicate comparisonPredicate = new ColumnLiteralComparisonPredicate(
                AbstractComparisonPredicate.Operator.EQUAL, column, literal);
        assertThat(new IndexColumnSelectionMatcher().matchComparison(comparisonPredicate), equalTo(true));
    }

    @Test
    void testNonMatch() {
        final ColumnMapping column = getColumnMappingExample().build();
        final SqlLiteralString literal = new SqlLiteralString("valueToCompareTo");
        final ColumnLiteralComparisonPredicate comparisonPredicate = new ColumnLiteralComparisonPredicate(
                AbstractComparisonPredicate.Operator.EQUAL, column, literal);
        assertThat(new IndexColumnSelectionMatcher().matchComparison(comparisonPredicate), equalTo(false));
    }
}