package com.exasol.adapter.document.edml;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base for EDML mappings that map to an Exasol column.
 */
@SuperBuilder
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
abstract class AbstractToColumnMapping implements MappingDefinition {
    private final String destinationName;
    private final String description;
    @Builder.Default
    private final KeyType key = KeyType.NONE;
    @Builder.Default
    private final boolean required = false;
}
