package com.exasol.adapter.document.literalconverter;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.sql.*;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.*;

/**
 * This class converts a {@link SqlNode} literal into a {@link ValueExpression}.
 */
public class SqlLiteralToValueExpressionConverter {
    private static final SqlLiteralToValueExpressionConverter INSTANCE = new SqlLiteralToValueExpressionConverter();

    /**
     * Private constructor to hide the public default.
     */
    private SqlLiteralToValueExpressionConverter() {
        // empty on purpose
    }

    /**
     * Get singleton instance of the {@link SqlLiteralToValueExpressionConverter}.
     *
     * @return instance of the {@link SqlLiteralToValueExpressionConverter}
     */
    public static SqlLiteralToValueExpressionConverter getInstance() {
        return INSTANCE;
    }

    /**
     * Converts a {@link SqlNode} literal into a {@link ValueExpression}.
     *
     * @param sqlNode to convert
     * @return {@link ValueExpression} with the literal value
     */
    public ValueExpression convert(final SqlNode sqlNode) {
        final Visitor visitor = new Visitor();
        try {
            sqlNode.accept(visitor);
            return visitor.getResult();
        } catch (final AdapterException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-60").message("Unexpected adapter exception.")
                    .ticketMitigation().toString(), exception);
        }
    }

    private static class Visitor extends VoidSqlNodeVisitor {
        private ValueExpression result;

        @Override
        public Void visit(final SqlLiteralBool sqlLiteralBool) {
            this.result = BooleanLiteral.of(sqlLiteralBool.getValue());
            return null;
        }

        @Override
        public Void visit(final SqlLiteralDate sqlLiteralDate) {
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-61").message(
                    "DateLiterals are not yet supported. This should not be possible since the corresponding capability is not set.")
                    .ticketMitigation().toString());
        }

        @Override
        public Void visit(final SqlLiteralDouble sqlLiteralDouble) {
            this.result = DoubleLiteral.of(sqlLiteralDouble.getValue());
            return null;
        }

        @Override
        public Void visit(final SqlLiteralExactnumeric sqlLiteralExactnumeric) {
            if (sqlLiteralExactnumeric.getValue().scale() == 0) {
                this.result = LongLiteral.of(sqlLiteralExactnumeric.getValue().longValue());
            } else {
                this.result = DoubleLiteral.of(sqlLiteralExactnumeric.getValue().doubleValue());
            }
            return null;
        }

        @Override
        public Void visit(final SqlLiteralNull sqlLiteralNull) {
            this.result = NullLiteral.nullLiteral();
            return null;
        }

        @Override
        public Void visit(final SqlLiteralString sqlLiteralString) {
            this.result = StringLiteral.of(sqlLiteralString.getValue());
            return null;
        }

        @Override
        public Void visit(final SqlLiteralTimestamp sqlLiteralTimestamp) {
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-64").message(
                    "Timestamp literals are not yet supported. This should however never happen since the corresponding capability is not set.")
                    .ticketMitigation().toString());
        }

        @Override
        public Void visit(final SqlLiteralTimestampUtc sqlLiteralTimestampUtc) {
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-62").message(
                    "Timestamp utc literals are not yet supported. This should however never happen since the corresponding capability is not set.")
                    .ticketMitigation().toString());
        }

        @Override
        public Void visit(final SqlLiteralInterval sqlLiteralInterval) {
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-63").message(
                    "Interval literals are not yet supported. This should however never happen since the corresponding capability is not set.")
                    .ticketMitigation().toString());
        }

        @Override
        public void visitUnimplemented() {
            throw new IllegalArgumentException(ExaError.messageBuilder("F-VSD-65")
                    .message("The given SqlNode is not a literal").ticketMitigation().toString());
        }

        public ValueExpression getResult() {
            return this.result;
        }
    }
}
