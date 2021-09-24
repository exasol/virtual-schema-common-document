package com.exasol.adapter.document.edml;

import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of the EDML {@code toDateMapping}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public final class ToDateMapping extends AbstractToColumnMapping {

    @Builder.Default
    private final ConvertableMappingErrorBehaviour notDateBehavior = ConvertableMappingErrorBehaviour.ABORT;

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
