package com.exasol.adapter.document.edml;

import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Java representation of the EDML {@code toBoolMapping}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@SuperBuilder
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public final class ToBoolMapping extends AbstractToColumnMapping {

    @Builder.Default
    private final ConvertableMappingErrorBehaviour notBooleanBehavior = ConvertableMappingErrorBehaviour.ABORT;

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
