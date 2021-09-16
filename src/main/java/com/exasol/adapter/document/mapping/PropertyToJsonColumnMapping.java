package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Maps a property of a DynamoDB table and all its descendants to a JSON string.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToJsonColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -929338332900021301L;
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
}
