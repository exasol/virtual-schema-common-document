package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

/**
 * This class describes a column that maps the source reference. For example in the files-virtual-schemas the name of
 * the file that contained the document. The name of this column is always {@code SOURCE_REFERENCE}, except from if it
 * is is used as foreign key (not yet possible).
 */
public final class SourceReferenceColumnMapping extends AbstractColumnMapping {
    public static final String DEFAULT_COLUMN_NAME = "SOURCE_REFERENCE";
    private static final long serialVersionUID = 9137288944756144081L;

    /**
     * Create an instance of {@link SourceReferenceColumnMapping}.
     */
    public SourceReferenceColumnMapping() {
        super(DEFAULT_COLUMN_NAME);
    }

    private SourceReferenceColumnMapping(final String exasolColumnName) {
        super(exasolColumnName);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createVarChar(2000, DataType.ExaCharset.UTF8);
    }

    @Override
    public boolean isExasolColumnNullable() {
        return false;
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return new SourceReferenceColumnMapping(newExasolName);
    }

    @Override
    public void accept(final ColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SourceReferenceColumnMapping)) {
            return false;
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
