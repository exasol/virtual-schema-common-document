package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a boolean value from the remote document and maps it to an Exasol
 * {@code BOOLEAN} column.
 */
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToBoolColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 7665762375515945443L;
    /** @serial */
    private final ConvertableMappingErrorBehaviour notBooleanBehavior;

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createBool();
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    @Override
    public String toString() {
        return "PropertyToBoolColumnMapping(super=" + super.toString() + ", notBooleanBehavior="
                + this.getNotBooleanBehavior() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(notBooleanBehavior);
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
        final PropertyToBoolColumnMapping other = (PropertyToBoolColumnMapping) obj;
        return notBooleanBehavior == other.notBooleanBehavior;
    }
}
