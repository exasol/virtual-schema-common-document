package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.metadata.DataType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * This class defines a column that maps the array index of a nested list. Such columns are useful for nested tables
 * that do not have an key.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
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
}
