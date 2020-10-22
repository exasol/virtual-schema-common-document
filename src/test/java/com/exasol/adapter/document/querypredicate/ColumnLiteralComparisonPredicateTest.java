package com.exasol.adapter.document.querypredicate;

import static com.exasol.EqualityMatchers.assertSymmetricEqualWithHashAndEquals;
import static com.exasol.EqualityMatchers.assertSymmetricNotEqualWithHashAndEquals;
import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.getColumnMappingExample;
import static com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate.Operator.NOT_EQUAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.sql.SqlLiteralString;

class ColumnLiteralComparisonPredicateTest {
    private static final AbstractComparisonPredicate.Operator OPERATOR = AbstractComparisonPredicate.Operator.EQUAL;
    private static final SqlLiteralString LITERAL = new SqlLiteralString("test");
    private static final ColumnMapping COLUMN = getColumnMappingExample().build();
    private static final ColumnLiteralComparisonPredicate TEST_PREDICATE = new ColumnLiteralComparisonPredicate(
            OPERATOR, COLUMN, LITERAL);

    @Test
    void testGetColumn() {
        assertThat(TEST_PREDICATE.getColumn(), equalTo(COLUMN));
    }

    @Test
    void testGetLiteral() {
        assertThat(TEST_PREDICATE.getLiteral(), equalTo(LITERAL));
    }

    @Test
    void testGetOperator() {
        assertThat(TEST_PREDICATE.getOperator(), equalTo(OPERATOR));
    }

    @Test
    void testVisitor() {
        final PredicateTestVisitor visitor = new PredicateTestVisitor();
        TEST_PREDICATE.accept(visitor);
        assertThat(visitor.getVisited(), equalTo(PredicateTestVisitor.Visited.COMPARISON));
    }

    @Test
    void testIdentical() {
        assertSymmetricEqualWithHashAndEquals(TEST_PREDICATE, TEST_PREDICATE);
    }

    @Test
    void testEqual() {
        final ColumnLiteralComparisonPredicate otherPredicate = new ColumnLiteralComparisonPredicate(OPERATOR, COLUMN,
                LITERAL);
        assertSymmetricEqualWithHashAndEquals(TEST_PREDICATE, otherPredicate);
    }

    static Stream<Arguments> nonEqualPredicates() {
        return Stream.of(//
                Arguments.of(new Object()), //
                Arguments.of(new ColumnLiteralComparisonPredicate(OPERATOR, COLUMN, new SqlLiteralString("other"))), //
                Arguments.of(new ColumnLiteralComparisonPredicate(OPERATOR,
                        getColumnMappingExample().exasolColumnName("OTHER").build(), LITERAL)), //
                Arguments.of(new ColumnLiteralComparisonPredicate(NOT_EQUAL, COLUMN, LITERAL))//
        );
    }

    @ParameterizedTest
    @MethodSource("nonEqualPredicates")
    void testNotEqual(final Object otherPredicate) {
        assertSymmetricNotEqualWithHashAndEquals(TEST_PREDICATE, otherPredicate);
    }
}
