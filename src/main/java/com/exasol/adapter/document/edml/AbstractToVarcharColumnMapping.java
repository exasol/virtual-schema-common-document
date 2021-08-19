package com.exasol.adapter.document.edml;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base for EDML mappings that map to an Exasol VARCHAR column.
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
class AbstractToVarcharColumnMapping extends AbstractToColumnMapping {
    @Builder.Default
    protected final int varcharColumnSize = 254;
}
