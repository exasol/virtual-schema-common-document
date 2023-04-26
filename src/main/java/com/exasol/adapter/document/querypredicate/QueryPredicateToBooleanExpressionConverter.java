package com.exasol.adapter.document.querypredicate;

import static com.exasol.sql.expression.BooleanTerm.not;
import static com.exasol.sql.expression.ExpressionTerm.column;
import static com.exasol.sql.expression.comparison.SimpleComparisonOperator.*;

import com.exasol.adapter.document.literalconverter.SqlLiteralToValueExpressionConverter;
import com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate.Operator;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.*;
import com.exasol.sql.expression.comparison.SimpleComparison;
import com.exasol.sql.expression.comparison.SimpleComparisonOperator;
import com.exasol.sql.expression.literal.BooleanLiteral;

/**
 * This class converts a {@link QueryPredicate} class structure to a {@link BooleanExpression} for the
 * sql-statement-builder.
 */
public class QueryPredicateToBooleanExpressionConverter {

    /**
     * Convert a {@link QueryPredicate} to a {@link BooleanExpression}.
     * 
     * @param queryPredicate predicate to convert
     * @return converted {@link BooleanExpression}
     */
    public BooleanExpression convert(final QueryPredicate queryPredicate) {
        final Visitor visitor = new Visitor();
        queryPredicate.accept(visitor);
        return visitor.getResult();
    }

    private static class Visitor implements QueryPredicateVisitor, ComparisonPredicateVisitor {
        private BooleanExpression result;

        @Override
        public void visit(final ComparisonPredicate comparisonPredicate) {
            comparisonPredicate.accept((ComparisonPredicateVisitor) this);
        }

        @Override
        public void visit(final LogicalOperator logicalOperator) {
            final BooleanExpression[] convertedOperands = logicalOperator.getOperands().stream()
                    .map(this::callRecursive).toArray(BooleanExpression[]::new);
            if (logicalOperator.getOperator().equals(LogicalOperator.Operator.AND)) {
                this.result = new And(convertedOperands);
            } else {// OR
                this.result = new Or(convertedOperands);
            }
        }

        @Override
        public void visit(final NoPredicate noPredicate) {
            this.result = BooleanLiteral.of(true);
        }

        @Override
        public void visit(final NotPredicate notPredicate) {
            this.result = not(callRecursive(notPredicate.getPredicate()));
        }

        @Override
        public void visit(final ColumnLiteralComparisonPredicate columnLiteralComparisonPredicate) {
            final ValueExpression literal = SqlLiteralToValueExpressionConverter.getInstance()
                    .convert(columnLiteralComparisonPredicate.getLiteral());
            this.result = new SimpleComparison(convertOperator(columnLiteralComparisonPredicate.getOperator()),
                    column(columnLiteralComparisonPredicate.getColumn().getExasolColumnName()), literal);
        }

        private SimpleComparisonOperator convertOperator(final Operator operator) {
            switch (operator) {
            case GREATER:
                return GREATER_THAN;
            case GREATER_EQUAL:
                return GREATER_THAN_OR_EQUAL;
            case EQUAL:
                return EQUAL;
            case LESS_EQUAL:
                return LESS_THAN_OR_EQUAL;
            case LESS:
                return LESS_THAN;
            case NOT_EQUAL:
                return NOT_EQUAL;
            case LIKE:
            case NOT_LIKE:
                throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-5").message(
                        "For efficiency reasons virtual schemas support operators LIKE and NOT LIKE only for column SOURCE_REFERENCE.")
                        .mitigation("Please change your query and wrap it in an outer SELECT statement that can use operator LIKE for other columns as well.")
                        .toString());
            default:
                throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-4")
                        .message("Converting {{OPERATOR}} is not yet implemented.", operator).ticketMitigation()
                        .toString());
            }
        }

        private BooleanExpression callRecursive(final QueryPredicate predicate) {
            final Visitor visitor = new Visitor();
            predicate.accept(visitor);
            return visitor.result;
        }

        public BooleanExpression getResult() {
            return this.result;
        }
    }
}
