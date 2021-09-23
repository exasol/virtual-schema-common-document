package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a date value from the remote document and maps it to an Exasol
 * {@code DATE} column.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToDateColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -3248941004767512863L;
    /** @serial */
    private final ConvertableMappingErrorBehaviour notDateBehaviour;

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createDate();
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }
}
