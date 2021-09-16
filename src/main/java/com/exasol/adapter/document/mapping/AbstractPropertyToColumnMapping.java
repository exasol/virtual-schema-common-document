package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This class is an abstract basis for {@link PropertyToColumnMapping}s.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
abstract class AbstractPropertyToColumnMapping extends AbstractColumnMapping implements PropertyToColumnMapping {
    private static final long serialVersionUID = 5820053202696971898L;
    private final DocumentPathExpression pathToSourceProperty;
    private final MappingErrorBehaviour lookupFailBehaviour;

    @Override
    public boolean isExasolColumnNullable() {
        return true;
    }
}
