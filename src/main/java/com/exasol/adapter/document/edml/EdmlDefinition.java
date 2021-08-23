package com.exasol.adapter.document.edml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of an EDML definition file.
 */
@Builder
@Jacksonized
@Data
public class EdmlDefinition {
    /**
     * Reference to the EDML schema.
     */
    @JsonProperty("$schema")
    private final String schema;
    @NonNull
    private final String source;
    @NonNull
    private final String destinationTable;
    @Builder.Default
    private final String description = "";
    @Builder.Default
    private final boolean addSourceReferenceColumn = false;
    @NonNull
    private final MappingDefinition mapping;
}
