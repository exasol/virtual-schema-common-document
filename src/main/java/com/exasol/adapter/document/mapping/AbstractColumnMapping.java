package com.exasol.adapter.document.mapping;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * This class is an abstract basis for {@link ColumnMapping}s.
 */
@EqualsAndHashCode
@Data
@SuperBuilder(toBuilder = true)
abstract class AbstractColumnMapping implements ColumnMapping {
    private static final long serialVersionUID = -3284843747319182683L;
    private final String exasolColumnName;
}
