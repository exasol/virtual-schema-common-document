package com.exasol.adapter.document.mapping;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base for {@link AbstractPropertyToColumnMapping}s that map to numeric Exasol columns.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public abstract class AbstractPropertyToNumberColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -1043554719625974481L;
    /**
     * @serial
     */
    private final MappingErrorBehaviour overflowBehaviour;
    /**
     * @serial
     */
    private final ConvertableMappingErrorBehaviour notNumericBehaviour;
}
