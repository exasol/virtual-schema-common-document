package com.exasol.adapter.document.edml;

import static com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT;
import static com.exasol.adapter.document.mapping.TruncateableMappingErrorBehaviour.TRUNCATE;

import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.mapping.TruncateableMappingErrorBehaviour;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Java representation of the EDML {@code toVarcharMapping}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data

@SuperBuilder
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public final class ToVarcharMapping extends AbstractToVarcharColumnMapping {
    @lombok.Builder.Default
    private final ConvertableMappingErrorBehaviour nonStringBehaviour = CONVERT_OR_ABORT;
    @lombok.Builder.Default
    private final TruncateableMappingErrorBehaviour overflowBehaviour = TRUNCATE;

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
