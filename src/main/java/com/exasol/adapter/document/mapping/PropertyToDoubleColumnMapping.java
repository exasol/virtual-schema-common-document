package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a floating-point number from the remote document and maps it to an Exasol
 * {@code DOUBLE-PRECISION} column.
 */
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToDoubleColumnMapping extends AbstractPropertyToNumberColumnMapping {
    private static final long serialVersionUID = 6021806680404016343L;

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createDouble();
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    @Override
    public String toString() {
        return "PropertyToDoubleColumnMapping(super=" + super.toString() + ")";
    }

    @Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PropertyToDoubleColumnMapping)) {
            return false;
        }
        if (!(this instanceof PropertyToDoubleColumnMapping)) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
