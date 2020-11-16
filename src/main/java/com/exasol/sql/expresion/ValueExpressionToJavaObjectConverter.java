package com.exasol.sql.expresion;

import java.math.BigDecimal;

import com.exasol.errorreporting.ExaError;
import com.exasol.sql.UnnamedPlaceholder;
import com.exasol.sql.expression.*;
import com.exasol.sql.expression.function.exasol.CastExasolFunction;
import com.exasol.sql.expression.function.exasol.ExasolFunction;
import com.exasol.sql.expression.function.exasol.ExasolUdf;

/**
 * This class converts {@link ValueExpression}s to java objects for the use in UDF emit functions. A description for the
 * data type can be found at https://docs.exasol.com/database_concepts/udf_scripts/java.htm - Parameters
 */
public class ValueExpressionToJavaObjectConverter {

    /**
     * Convert {@link ValueExpression}s to java objects for the use in the UDF emit function.
     * 
     * @param valueExpression value expression to be converted
     * @return java value
     */
    public Object convert(final ValueExpression valueExpression) {
        final Visitor visitor = new Visitor();
        valueExpression.accept(visitor);
        return visitor.getResult();
    }

    private static class Visitor implements ValueExpressionVisitor {
        private Object result;

        @Override
        public void visit(final UnnamedPlaceholder unnamedPlaceholder) {
            unsupported("UnnamedPlaceholder");
        }

        @Override
        public void visit(final StringLiteral literal) {
            this.result = literal.toString();
        }

        @Override
        public void visit(final IntegerLiteral literal) {
            this.result = literal.getValue();
        }

        @Override
        public void visit(final LongLiteral literal) {
            this.result = literal.getValue();
        }

        @Override
        public void visit(final DoubleLiteral literal) {
            this.result = BigDecimal.valueOf(literal.getValue());
        }

        @Override
        public void visit(final FloatLiteral literal) {
            this.result = BigDecimal.valueOf(literal.getValue());
        }

        @Override
        public void visit(final BigDecimalLiteral literal) {
            this.result = literal.getValue();
        }

        @Override
        public void visit(final BooleanLiteral literal) {
            this.result = literal.toBoolean();
        }

        @Override
        public void visit(final ColumnReference columnReference) {
            unsupported("ColumnReference");
        }

        @Override
        public void visit(final DefaultValue defaultValue) {
            unsupported("DefaultValue");
        }

        @Override
        public void visit(final ExasolFunction function) {
            unsupported("ExasolFunction");
        }

        @Override
        public void leave(final ExasolFunction function) {
            unsupported("ExasolFunction");
        }

        @Override
        public void visit(final ExasolUdf function) {
            unsupported("ExasolUdf");
        }

        @Override
        public void leave(final ExasolUdf function) {
            unsupported("ExasolUdf");
        }

        @Override
        public void visit(final BinaryArithmeticExpression expression) {
            unsupported("BinaryArithmeticExpression");
        }

        @Override
        public void visit(final NullLiteral nullLiteral) {
            this.result = null;
        }

        @Override
        public void visit(final BooleanExpression booleanExpression) {
            unsupported("boolean expression");
        }

        @Override
        public void visit(final CastExasolFunction castFunction) {
            unsupported("cast function");
        }

        private void unsupported(final String type) {
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-70")
                    .message("The ValueExpression {{TYPE}} has no Java Value equivalent.").parameter("TYPE", type)
                    .ticketMitigation().toString());
        }

        public Object getResult() {
            return this.result;
        }
    }
}
