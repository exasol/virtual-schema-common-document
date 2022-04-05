package com.exasol.adapter.document.edml;

import lombok.*;

/**
 * Java representation of an EDML definition file.
 */
@Builder
@Data
public class EdmlDefinition {
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
