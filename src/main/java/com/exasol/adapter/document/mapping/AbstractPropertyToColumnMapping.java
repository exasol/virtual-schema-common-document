package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * This class is an abstract basis for {@link PropertyToColumnMapping}s.
 */
@ToString(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
abstract class AbstractPropertyToColumnMapping extends AbstractColumnMapping implements PropertyToColumnMapping {
    private static final long serialVersionUID = -5125991213244975414L;
    private final DocumentPathExpression pathToSourceProperty;
    private final MappingErrorBehaviour lookupFailBehaviour;

    @Override
    public boolean isExasolColumnNullable() {
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(pathToSourceProperty, lookupFailBehaviour);
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
        final AbstractPropertyToColumnMapping other = (AbstractPropertyToColumnMapping) obj;
        return Objects.equals(pathToSourceProperty, other.pathToSourceProperty)
                && lookupFailBehaviour == other.lookupFailBehaviour;
    }
}
