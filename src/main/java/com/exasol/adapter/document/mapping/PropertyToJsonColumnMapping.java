package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Maps a property of a DynamoDB table and all its descendants to a JSON string.
 */
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToJsonColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -6633690614095755071L;
    /** @serial */
    private final int varcharColumnSize;
    /** @serial */
    private final MappingErrorBehaviour overflowBehaviour;

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return toBuilder().exasolColumnName(newExasolName).build();
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createVarChar(this.varcharColumnSize, DataType.ExaCharset.UTF8);
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "PropertyToJsonColumnMapping(super=" + super.toString() + ", varcharColumnSize="
                + this.getVarcharColumnSize() + ", overflowBehaviour=" + this.getOverflowBehaviour() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(varcharColumnSize, overflowBehaviour);
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
        final PropertyToJsonColumnMapping other = (PropertyToJsonColumnMapping) obj;
        return varcharColumnSize == other.varcharColumnSize && overflowBehaviour == other.overflowBehaviour;
    }
}
