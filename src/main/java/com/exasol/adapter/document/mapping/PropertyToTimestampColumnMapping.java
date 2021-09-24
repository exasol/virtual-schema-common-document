package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a timestamp value from the remote document and maps it to an Exasol
 * {@code TIMESTAMP} or {@code TIMESTAMP WITH LOCAL TIMEZONE} column.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToTimestampColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -2269991319384064669L;
    /** @serial */
    private final ConvertableMappingErrorBehaviour notTimestampBehaviour;
    /** @serial */
    private final boolean useTimestampWithLocalTimezoneType;

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createTimestamp(this.useTimestampWithLocalTimezoneType);
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }
}
