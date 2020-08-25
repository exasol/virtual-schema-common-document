package com.exasol.adapter.document.queryplanning.selectionextractor;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.getColumnMappingExample;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.PropertyToColumnMapping;
import com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate;
import com.exasol.adapter.document.querypredicate.ColumnLiteralComparisonPredicate;
import com.exasol.adapter.sql.SqlLiteralString;

class NestedColumnSelectionMatcherTest {

    @Test
    void testMatch() {
        final PropertyToColumnMapping nestedColumn = getColumnMappingExample().pathToSourceProperty(
                DocumentPathExpression.builder().addObjectLookup("test").addArrayAll().addObjectLookup("value").build())
                .build();
        final ColumnLiteralComparisonPredicate comparison = new ColumnLiteralComparisonPredicate(
                AbstractComparisonPredicate.Operator.EQUAL, nestedColumn, new SqlLiteralString(""));
        assertThat(new NestedColumnSelectionMatcher().matchComparison(comparison), equalTo(true));
    }

    @Test
    void testNotMatch() {
        final PropertyToColumnMapping nestedColumn = getColumnMappingExample()
                .pathToSourceProperty(
                        DocumentPathExpression.builder().addObjectLookup("test").addObjectLookup("value").build())
                .build();
        final ColumnLiteralComparisonPredicate comparison = new ColumnLiteralComparisonPredicate(
                AbstractComparisonPredicate.Operator.EQUAL, nestedColumn, new SqlLiteralString(""));
        assertThat(new NestedColumnSelectionMatcher().matchComparison(comparison), equalTo(false));
    }
}