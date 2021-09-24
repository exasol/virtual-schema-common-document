package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a boolean value from the remote document and maps it to an Exasol
 * {@code BOOLEAN} column.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToBoolColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 4835013551698610917L;
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
}