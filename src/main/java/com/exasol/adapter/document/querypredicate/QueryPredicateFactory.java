package com.exasol.adapter.document.querypredicate;

import static com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate.LIKE_ESCAPE_CHAR;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.SchemaMappingToSchemaMetadataConverter;
import com.exasol.adapter.sql.*;
import com.exasol.errorreporting.ExaError;

/**
 * This class builds a{@link QueryPredicate} structure from a {@link SqlStatementSelect}s where clause. The new
 * structure represents the same conditional logic but uses deserialized column definitions, literals of the remote
 * database and is serializable.
 */
public class QueryPredicateFactory {
    private static final QueryPredicateFactory INSTANCE = new QueryPredicateFactory();

    /**
     * Empty constructor to hide the public default.
     */
    private QueryPredicateFactory() {
        // empty on purpose.
    }

    /**
     * Get a singleton instance of {@link QueryPredicateFactory}.
     *
     * @return instance of {@link QueryPredicateFactory}
     */
    public static QueryPredicateFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Converts the given SQL predicate into a {@link QueryPredicate} structure.
     * 
     * @param sqlPredicate SQL predicate to convert
     * @return {@link QueryPredicate} structure
     */
    public QueryPredicate buildPredicateFor(final SqlNode sqlPredicate) {
        if (sqlPredicate == null) {
            return new NoPredicate();
        } else {
            final Visitor visitor = new Visitor();
            try {
                sqlPredicate.accept(visitor);
            } catch (final AdapterException exception) {
                // This should never happen, as we do not throw adapter exceptions in the visitor.
                throw new IllegalStateException(ExaError.messageBuilder("F-VSD-39")
                        .message("An unexpected adapter exception occurred.").ticketMitigation().toString(), exception);
            }
            return visitor.getPredicate();
        }
    }

    private static class Visitor extends VoidSqlNodeVisitor {
        private QueryPredicate predicate;

        @Override
        public Void visit(final SqlPredicateEqual sqlPredicateEqual) {
            buildComparison(sqlPredicateEqual, AbstractComparisonPredicate.Operator.EQUAL);
            return null;
        }

        @Override
        public Void visit(final SqlPredicateLess sqlPredicateLess) {
            buildComparison(sqlPredicateLess, AbstractComparisonPredicate.Operator.LESS);
            return null;
        }

        @Override
        public Void visit(final SqlPredicateLessEqual sqlPredicateLessEqual) {
            buildComparison(sqlPredicateLessEqual, AbstractComparisonPredicate.Operator.LESS_EQUAL);
            return null;
        }

        @Override
        public Void visit(final SqlPredicateLike sqlPredicateLike) {
            final SqlNode left = sqlPredicateLike.getLeft();
            final SqlNode pattern = sqlPredicateLike.getPattern();
            if (!(left instanceof SqlColumn) || pattern instanceof SqlColumn) {
                throw new UnsupportedOperationException(ExaError.messageBuilder("E-VSD-1").message(
                        "This version of the document virtual schemas only supports LIKE operators in the form <column> LIKE <literal>. Other formats are not supported.")
                        .mitigation("Please change your query.").toString());
            }
            final SqlNode escapeChar = sqlPredicateLike.getEscapeChar();
            if (escapeChar != null && !escapeChar.toString().equals(LIKE_ESCAPE_CHAR)) {
                throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-99")
                        .message("This virtual-schema only supports LIKE predicates with '\\' as escape character.")
                        .mitigation("Please add ESCAPE '\\'.").toString());
            }
            buildColumnLiteralComparision((SqlColumn) left, pattern, AbstractComparisonPredicate.Operator.LIKE);
            return null;
        }

        @Override
        public Void visit(final SqlPredicateNotEqual sqlPredicateNotEqual) {
            buildComparison(sqlPredicateNotEqual, AbstractComparisonPredicate.Operator.NOT_EQUAL);
            return null;
        }

        void buildComparison(final AbstractSqlBinaryEquality sqlEquality,
                final AbstractComparisonPredicate.Operator operator) {
            final SqlNode left = sqlEquality.getLeft();
            final SqlNode right = sqlEquality.getRight();
            if (left instanceof SqlColumn && right instanceof SqlColumn) {
                throw new UnsupportedOperationException(ExaError.messageBuilder("E-VSD-40")
                        .message("Predicates on two columns are not yet supported in this Virtual Schema version.")
                        .mitigation("Change your query.").toString());
            } else if (right instanceof SqlColumn) {
                buildColumnLiteralComparision((SqlColumn) right, left, mirrorOperator(operator));
            } else if (left instanceof SqlColumn) {
                buildColumnLiteralComparision((SqlColumn) left, right, operator);
            } else {
                throw new UnsupportedOperationException(ExaError.messageBuilder("E-VSD-41")
                        .message("Predicates on two literals are not yet supported in this Virtual Schema version.")
                        .mitigation("Change your query.").toString());
            }
        }

        private AbstractComparisonPredicate.Operator mirrorOperator(
                final AbstractComparisonPredicate.Operator operator) {
            switch (operator) {
            case LESS:
                return AbstractComparisonPredicate.Operator.GREATER;
            case LESS_EQUAL:
                return AbstractComparisonPredicate.Operator.GREATER_EQUAL;
            case GREATER:
                return AbstractComparisonPredicate.Operator.LESS;
            case GREATER_EQUAL:
                return AbstractComparisonPredicate.Operator.LESS_EQUAL;
            case EQUAL:
                return AbstractComparisonPredicate.Operator.EQUAL;
            case NOT_EQUAL:
                return AbstractComparisonPredicate.Operator.NOT_EQUAL;
            default:
                throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-2").message(
                        "Unimplemented operator: {{OPERATOR}}. Actually the database should not have passed this operator here, since this adapter does not have the corresponding capabilities set.")
                        .parameter("OPERATOR", operator.toString()).ticketMitigation().toString());
            }
        }

        void buildColumnLiteralComparision(final SqlColumn column, final SqlNode literal,
                final AbstractComparisonPredicate.Operator operator) {
            final ColumnMapping columnMapping = new SchemaMappingToSchemaMetadataConverter()
                    .convertBackColumn(column.getMetadata());
            this.predicate = new ColumnLiteralComparisonPredicate(operator, columnMapping, literal);
        }

        @Override
        public Void visit(final SqlPredicateAnd sqlPredicateAnd) {
            this.predicate = new LogicalOperator(convertPredicates(sqlPredicateAnd.getAndedPredicates()),
                    LogicalOperator.Operator.AND);
            return null;
        }

        @Override
        public Void visit(final SqlPredicateOr sqlPredicateOr) {
            this.predicate = new LogicalOperator(convertPredicates(sqlPredicateOr.getOrPredicates()),
                    LogicalOperator.Operator.OR);
            return null;
        }

        @Override
        public Void visit(final SqlPredicateNot sqlPredicateNot) {
            final QueryPredicateFactory queryPredicateFactory = new QueryPredicateFactory();
            this.predicate = new NotPredicate(queryPredicateFactory.buildPredicateFor(sqlPredicateNot.getExpression()));
            return null;
        }

        private Set<QueryPredicate> convertPredicates(final List<SqlNode> sqlPredicates) {
            final QueryPredicateFactory queryPredicateFactory = new QueryPredicateFactory();
            return sqlPredicates.stream().map(queryPredicateFactory::buildPredicateFor).collect(Collectors.toSet());
        }

        private QueryPredicate getPredicate() {
            return this.predicate;
        }
    }
}
