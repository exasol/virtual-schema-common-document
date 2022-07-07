package com.exasol.adapter.document.querypredicate;

import static com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate.Operator.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.sql.SqlLiteralString;
import com.exasol.sql.expression.*;
import com.exasol.sql.expression.comparison.Comparison;
import com.exasol.sql.expression.comparison.ComparisonOperator;
import com.exasol.sql.expression.comparison.SimpleComparisonOperator;
import com.exasol.sql.expression.literal.BooleanLiteral;

class QueryPredicateToBooleanExpressionConverterTest {

    private static final QueryPredicateToBooleanExpressionConverter CONVERTER = new QueryPredicateToBooleanExpressionConverter();

    static Stream<Arguments> expectedOperatorMapping() {
        return Stream.of(//
                Arguments.of(EQUAL, SimpleComparisonOperator.EQUAL), //
                Arguments.of(LESS, SimpleComparisonOperator.LESS_THAN), //
                Arguments.of(LESS_EQUAL, SimpleComparisonOperator.LESS_THAN_OR_EQUAL), //
                Arguments.of(GREATER, SimpleComparisonOperator.GREATER_THAN), //
                Arguments.of(GREATER_EQUAL, SimpleComparisonOperator.GREATER_THAN_OR_EQUAL), //
                Arguments.of(AbstractComparisonPredicate.Operator.NOT_EQUAL, SimpleComparisonOperator.NOT_EQUAL)//
        );
    }

    @ParameterizedTest
    @MethodSource("expectedOperatorMapping")
    void testConvertDifferentOperators(final AbstractComparisonPredicate.Operator operator,
            final ComparisonOperator expectedOperator) {
        final ColumnMapping column = getMockColumn("TEST");
        final Comparison result = (Comparison) CONVERTER
                .convert(new ColumnLiteralComparisonPredicate(operator, column, new SqlLiteralString("")));
        final ColumnReference columnReference = (ColumnReference) result.getLeftOperand();
        assertAll(//
                () -> assertThat(columnReference.getColumnName(), equalTo("TEST")),
                () -> assertThat(result.getOperator(), equalTo(expectedOperator))//
        );
    }

    private ColumnMapping getMockColumn(final String columnName) {
        final ColumnMapping column = mock(ColumnMapping.class);
        when(column.getExasolColumnName()).thenReturn(columnName);
        return column;
    }

    @Test
    void testConvertAnd() {
        final ColumnMapping column = getMockColumn("COLUMN");
        final ColumnLiteralComparisonPredicate comparison1 = new ColumnLiteralComparisonPredicate(EQUAL, column,
                new SqlLiteralString(""));
        final ColumnLiteralComparisonPredicate comparison2 = new ColumnLiteralComparisonPredicate(GREATER, column,
                new SqlLiteralString(""));
        final LogicalOperator logicalOperator = new LogicalOperator(Set.of(comparison1, comparison2),
                LogicalOperator.Operator.AND);
        final And and = (And) CONVERTER.convert(logicalOperator);
        assertThat(and.getOperands().size(), equalTo(2));
    }

    @Test
    void testConvertOr() {
        final ColumnMapping column = getMockColumn("COLUMN");
        final ColumnLiteralComparisonPredicate comparison1 = new ColumnLiteralComparisonPredicate(EQUAL, column,
                new SqlLiteralString(""));
        final ColumnLiteralComparisonPredicate comparison2 = new ColumnLiteralComparisonPredicate(GREATER, column,
                new SqlLiteralString(""));
        final LogicalOperator logicalOperator = new LogicalOperator(Set.of(comparison1, comparison2),
                LogicalOperator.Operator.OR);
        final Or or = (Or) CONVERTER.convert(logicalOperator);
        assertThat(or.getOperands().size(), equalTo(2));
    }

    @Test
    void testConvertNoPredicate() {
        final BooleanLiteral result = (BooleanLiteral) CONVERTER.convert(new NoPredicate());
        assertThat(result.toBoolean(), equalTo(true));
    }

    @Test
    void testConvertNotPredicate() {
        final BooleanExpression result = CONVERTER.convert(new NotPredicate(new NoPredicate()));
        assertThat(result, instanceOf(Not.class));
    }
}