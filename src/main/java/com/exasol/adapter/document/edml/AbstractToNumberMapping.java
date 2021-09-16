package com.exasol.adapter.document.edml;

import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.mapping.MappingErrorBehaviour;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base for EDML mappings that map to a numeric Exasol column.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public abstract class AbstractToNumberMapping extends AbstractToColumnMapping {
    @Builder.Default
    private final MappingErrorBehaviour overflowBehaviour = MappingErrorBehaviour.ABORT;
    @Builder.Default
    private final ConvertableMappingErrorBehaviour notNumericBehaviour = ConvertableMappingErrorBehaviour.ABORT;
}
