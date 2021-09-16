package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a string from the remote document and maps it to an Exasol VARCHAR column.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToVarcharColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -8517627343996328336L;//
    /** @serial */
    private final int varcharColumnSize;
    /** @serial */
    private final TruncateableMappingErrorBehaviour overflowBehaviour;
    /** @serial */
    private final ConvertableMappingErrorBehaviour nonStringBehaviour;

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
}
