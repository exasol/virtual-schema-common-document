package com.exasol.adapter.document.edml;

import com.exasol.adapter.document.mapping.MappingErrorBehaviour;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of the EDML {@code toDecimalMapping}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public final class ToDecimalMapping extends AbstractToColumnMapping {
    @Builder.Default
    private final int decimalPrecision = 18;
    @Builder.Default
    private final int decimalScale = 0;
    @Builder.Default
    private final MappingErrorBehaviour overflowBehaviour = MappingErrorBehaviour.ABORT;
    @Builder.Default
    private final MappingErrorBehaviour notNumericBehaviour = MappingErrorBehaviour.ABORT;

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
