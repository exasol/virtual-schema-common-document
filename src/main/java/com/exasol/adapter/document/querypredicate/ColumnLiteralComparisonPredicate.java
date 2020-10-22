package com.exasol.adapter.document.querypredicate;

import java.util.List;
import java.util.Objects;

import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.sql.SqlNode;

/**
 * This class represents a comparison between a literal and a column of a table.
 */
public class ColumnLiteralComparisonPredicate extends AbstractComparisonPredicate {
    private static final long serialVersionUID = 1747022926992293431L;//
    private final SqlNode literal;
    private final ColumnMapping column;

    /**
     * Create an instance of {@link ColumnLiteralComparisonPredicate}.
     * 
     * @param operator comparison operator
     * @param column   column to compare
     * @param literal  literal to compare
     */
    public ColumnLiteralComparisonPredicate(final Operator operator, final ColumnMapping column,
            final SqlNode literal) {
        super(operator);
        this.literal = literal;
        this.column = column;
    }

    /**
     * Get the literal that the column is compared to in this predicate.
     * 
     * @return literal
     */
    public SqlNode getLiteral() {
        return this.literal;
    }

    /**
     * Get the column that the literal is compared to in this predicate.
     * 
     * @return column
     */
    public ColumnMapping getColumn() {
        return this.column;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ColumnLiteralComparisonPredicate)) {
            return false;
        }
        final ColumnLiteralComparisonPredicate that = (ColumnLiteralComparisonPredicate) other;
        return super.equals(other) && this.literal.equals(that.literal) && this.column.equals(that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.literal.toString(), this.column.hashCode(), super.hashCode());
    }

    @Override
    public String toString() {
        return this.column.getExasolColumnName() + super.toString() + this.literal.toString();
    }

    @Override
    public void accept(final ComparisonPredicateVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<ColumnMapping> getComparedColumns() {
        return List.of(this.column);
    }
}
