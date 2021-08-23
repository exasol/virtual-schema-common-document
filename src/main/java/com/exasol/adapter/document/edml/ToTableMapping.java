package com.exasol.adapter.document.edml;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of the EDML toTableMapping.
 */
@Builder
@Jacksonized
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public class ToTableMapping implements MappingDefinition {
    private final String destinationTable;
    @NonNull
    private final MappingDefinition mapping;
    @Builder.Default
    private final String description = "";
}
