package com.exasol.adapter.document.edml;

import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of the EDML {@code toTimestampMapping}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public final class ToTimestampMapping extends AbstractToColumnMapping {

    @Builder.Default
    private final ConvertableMappingErrorBehaviour notTimestampBehavior = ConvertableMappingErrorBehaviour.ABORT;

    /**
     * If set to {@code true} this adapter will use a {@code TIMESTAMP WITH LOCAL TIMEZONE} Exasol column instead of a
     * {@code TIMEZONE} column.
     */
    @Builder.Default
    private final boolean useTimestampWithLocalTimezoneType = true;

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
