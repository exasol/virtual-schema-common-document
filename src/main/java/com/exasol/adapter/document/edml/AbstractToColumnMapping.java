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
class AbstractToColumnMapping implements MappingDefinition {
    protected final String destinationName;
    protected final String description;
    @Builder.Default
    protected final KeyType key = KeyType.NONE;
}
