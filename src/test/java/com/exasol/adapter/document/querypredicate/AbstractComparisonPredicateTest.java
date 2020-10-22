package com.exasol.adapter.document.querypredicate;

import static com.exasol.EqualityMatchers.assertSymmetricEqualWithHashAndEquals;
import static com.exasol.EqualityMatchers.assertSymmetricNotEqualWithHashAndEquals;
import static com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate.Operator.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.document.mapping.ColumnMapping;

class AbstractComparisonPredicateTest {

    public static final AbstractComparisonPredicate EQUAL_COMPARISON = new Stub(EQUAL);

    static Stream<Arguments> expectedToStringResult() {
        return Stream.of(//
                Arguments.of(EQUAL, "="), //
                Arguments.of(LESS, "<"), //
                Arguments.of(LESS_EQUAL, "<="), //
                Arguments.of(GREATER, ">"), //
                Arguments.of(GREATER_EQUAL, ">="), //
                Arguments.of(NOT_EQUAL, "!="), //
                Arguments.of(LIKE, "LIKE")//
        );
    }

    static Stream<Arguments> getOtherObjects() {
        return Stream.of(//
                Arguments.of(new Object()), //
                Arguments.of(new Stub(LESS)));
    }

    @ParameterizedTest
    @MethodSource("expectedToStringResult")
    void testToString(final AbstractComparisonPredicate.Operator operator, final String expectedString) {
        final AbstractComparisonPredicate mock = new Stub(operator);
        assertThat(mock.toString(), equalTo(expectedString));
    }

    @Test
    void testGetOperator() {
        assertThat(EQUAL_COMPARISON.getOperator(), equalTo(EQUAL));
    }

    @Test
    void testIdentical() {
        assertSymmetricEqualWithHashAndEquals(EQUAL_COMPARISON, EQUAL_COMPARISON);
    }

    @Test
    void testEqual() {
        assertSymmetricEqualWithHashAndEquals(EQUAL_COMPARISON, new Stub(EQUAL));
    }

    @ParameterizedTest
    @MethodSource("getOtherObjects")
    void testNotEqual(final Object other) {
        assertSymmetricNotEqualWithHashAndEquals(EQUAL_COMPARISON, other);
    }

    private static class Stub extends AbstractComparisonPredicate {

        public Stub(final Operator operator) {
            super(operator);
        }

        @Override
        public void accept(final ComparisonPredicateVisitor visitor) {

        }

        @Override
        public List<ColumnMapping> getComparedColumns() {
            return null;
        }

        @Override
        public boolean equals(final Object other) {
            return super.equals(other);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}