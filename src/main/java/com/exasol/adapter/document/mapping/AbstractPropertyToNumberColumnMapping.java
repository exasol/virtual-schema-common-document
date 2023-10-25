package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base for {@link AbstractPropertyToColumnMapping}s that map to numeric Exasol columns.
 */
@ToString(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public abstract class AbstractPropertyToNumberColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -3412527315242611386L;
    /**
     * @serial
     */
    private final MappingErrorBehaviour overflowBehaviour;
    /**
     * @serial
     */
    private final ConvertableMappingErrorBehaviour notNumericBehaviour;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(overflowBehaviour, notNumericBehaviour);
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
        final AbstractPropertyToNumberColumnMapping other = (AbstractPropertyToNumberColumnMapping) obj;
        return overflowBehaviour == other.overflowBehaviour && notNumericBehaviour == other.notNumericBehaviour;
    }
}
