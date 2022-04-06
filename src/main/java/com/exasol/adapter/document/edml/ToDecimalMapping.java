package com.exasol.adapter.document.edml;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Java representation of the EDML {@code toDecimalMapping}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public final class ToDecimalMapping extends AbstractToNumberMapping {
    @Builder.Default
    private final int decimalPrecision = 18;
    @Builder.Default
    private final int decimalScale = 0;

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
