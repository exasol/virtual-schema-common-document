package com.exasol.adapter.document.mapping;

import java.util.Objects;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * This class is an abstract basis for {@link ColumnMapping}s.
 */
@Data
@SuperBuilder(toBuilder = true)
abstract class AbstractColumnMapping implements ColumnMapping {
    private static final long serialVersionUID = -3284843747319182683L;
    private final String exasolColumnName;

    @Override
    public String toString() {
        return "AbstractColumnMapping(exasolColumnName=" + this.getExasolColumnName() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(exasolColumnName);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractColumnMapping other = (AbstractColumnMapping) obj;
        return Objects.equals(exasolColumnName, other.exasolColumnName);
    }
}
