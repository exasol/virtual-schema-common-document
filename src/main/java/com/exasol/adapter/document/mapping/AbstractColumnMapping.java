package com.exasol.adapter.document.mapping;

import lombok.EqualsAndHashCode;

/**
 * This class is an abstract basis for {@link ColumnMapping}s.
 */
@EqualsAndHashCode
abstract class AbstractColumnMapping implements ColumnMapping {
    private static final long serialVersionUID = -7175716931934556879L;
    private final String exasolColumnName;

    /**
     * Create an instance of {@link AbstractColumnMapping}
     *
     * @param exasolColumnName name of the Exasol column
     */
    protected AbstractColumnMapping(final String exasolColumnName) {
        this.exasolColumnName = exasolColumnName;
    }

    @Override
    public final String getExasolColumnName() {
        return this.exasolColumnName;
    }
}
