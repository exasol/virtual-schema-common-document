package com.exasol.adapter.document.edml;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base for EDML mappings that map to an Exasol {@code VARCHAR} column.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
abstract class AbstractToVarcharColumnMapping extends AbstractToColumnMapping {
    @Builder.Default
    protected final int varcharColumnSize = 254;
}
