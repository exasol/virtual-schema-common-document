package com.exasol.adapter.document.edml;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of the EDML {@code toDoubleMapping}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
@Data
public final class ToDoubleMapping extends AbstractToNumberMapping {

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
