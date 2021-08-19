package com.exasol.adapter.document.edml;

import static com.exasol.adapter.document.mapping.MappingErrorBehaviour.ABORT;

import com.exasol.adapter.document.mapping.MappingErrorBehaviour;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of the EDML toJsonMapping.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
@Data
public final class ToJsonMapping extends AbstractToVarcharColumnMapping {
    @Builder.Default
    private final MappingErrorBehaviour overflowBehaviour = ABORT;
}
