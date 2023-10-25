package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.metadata.DataType;

import lombok.experimental.SuperBuilder;

/**
 * This class defines a column that maps the array index of a nested list. Such columns are useful for nested tables
 * that do not have an key.
 */

@SuperBuilder(toBuilder = true)
public final class IterationIndexColumnMapping extends AbstractColumnMapping {
    private static final long serialVersionUID = -5720702055496015560L;
    /** @serial */
    private final DocumentPathExpression tablesPath;

    /**
     * Create an instance of {@link IterationIndexColumnMapping}.
     *
     * @param exasolColumnName name of the Exasol column
     * @param tablesPath       the path to the array that's row index is modeled using this column
     */
    public IterationIndexColumnMapping(final String exasolColumnName, final DocumentPathExpression tablesPath) {
        this(builder().exasolColumnName(exasolColumnName).tablesPath(tablesPath));
    }

    public DocumentPathExpression getTablesPath() {
        return tablesPath;
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createDecimal(9, 0);
    }

    @Override
    public boolean isExasolColumnNullable() {
        return false;
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    @Override
    public void accept(final ColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IterationIndexColumnMapping(super=" + super.toString() + ", tablesPath=" + this.getTablesPath() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(tablesPath);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IterationIndexColumnMapping other = (IterationIndexColumnMapping) obj;
        return Objects.equals(tablesPath, other.tablesPath);
    }
}
