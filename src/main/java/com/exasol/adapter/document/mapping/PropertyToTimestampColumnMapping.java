package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a timestamp value from the remote document and maps it to an Exasol
 * {@code TIMESTAMP} or {@code TIMESTAMP WITH LOCAL TIMEZONE} column.
 */
@SuperBuilder(toBuilder = true)
public final class PropertyToTimestampColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 2336854835413425711L;
    /** @serial */
    private final ConvertableMappingErrorBehaviour notTimestampBehaviour;
    /** @serial */
    private final boolean useTimestampWithLocalTimezoneType;

    public ConvertableMappingErrorBehaviour getNotTimestampBehaviour() {
        return notTimestampBehaviour;
    }

    public boolean isUseTimestampWithLocalTimezoneType() {
        return useTimestampWithLocalTimezoneType;
    }

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

    @Override
    public String toString() {
        return "PropertyToTimestampColumnMapping(super=" + super.toString() + ", notTimestampBehaviour="
                + this.getNotTimestampBehaviour() + ", useTimestampWithLocalTimezoneType="
                + this.isUseTimestampWithLocalTimezoneType() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(notTimestampBehaviour, useTimestampWithLocalTimezoneType);
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
        final PropertyToTimestampColumnMapping other = (PropertyToTimestampColumnMapping) obj;
        return notTimestampBehaviour == other.notTimestampBehaviour
                && useTimestampWithLocalTimezoneType == other.useTimestampWithLocalTimezoneType;
    }
}
