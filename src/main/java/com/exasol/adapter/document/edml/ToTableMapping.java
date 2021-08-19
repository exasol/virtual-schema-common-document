package com.exasol.adapter.document.edml;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

/**
 * Java representation of the EDML toTableMapping.
 */
@Builder
@Jacksonized
@Data
public class ToTableMapping implements MappingDefinition {
    String destinationTable;
    @NonNull
    MappingDefinition mapping;
    @Builder.Default
    String description = "";
}
