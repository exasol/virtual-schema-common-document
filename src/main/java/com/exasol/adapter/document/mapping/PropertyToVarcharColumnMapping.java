package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.edml.TruncateableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a string from the remote document and maps it to an Exasol VARCHAR column.
 */
@SuperBuilder(toBuilder = true)
public final class PropertyToVarcharColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 331013763747038031L;
    /** @serial */
    private final int varcharColumnSize;
    /** @serial */
    private final TruncateableMappingErrorBehaviour overflowBehaviour;
    /** @serial */
    private final ConvertableMappingErrorBehaviour nonStringBehaviour;

    public int getVarcharColumnSize() {
        return varcharColumnSize;
    }

    public TruncateableMappingErrorBehaviour getOverflowBehaviour() {
        return overflowBehaviour;
    }

    public ConvertableMappingErrorBehaviour getNonStringBehaviour() {
        return nonStringBehaviour;
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
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    @Override
    public String toString() {
        return "PropertyToVarcharColumnMapping(super=" + super.toString() + ", varcharColumnSize="
                + this.getVarcharColumnSize() + ", overflowBehaviour=" + this.getOverflowBehaviour()
                + ", nonStringBehaviour=" + this.getNonStringBehaviour() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(varcharColumnSize, overflowBehaviour, nonStringBehaviour);
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
        final PropertyToVarcharColumnMapping other = (PropertyToVarcharColumnMapping) obj;
        return varcharColumnSize == other.varcharColumnSize && overflowBehaviour == other.overflowBehaviour
                && nonStringBehaviour == other.nonStringBehaviour;
    }
}
