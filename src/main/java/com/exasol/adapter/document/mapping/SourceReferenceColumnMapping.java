package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.experimental.SuperBuilder;

/**
 * This class describes a column that maps the source reference. For example in the files-virtual-schemas the name of
 * the file that contained the document.
 * <p>
 * The name of this column is always {@code SOURCE_REFERENCE}, except from if it is used as foreign key (not yet
 * possible).
 * </p>
 */
@SuperBuilder(toBuilder = true)
public final class SourceReferenceColumnMapping extends AbstractColumnMapping {
    /** Constant for the default name of the SOURCE_REFERENCE column */
    public static final String DEFAULT_COLUMN_NAME = "SOURCE_REFERENCE";
    private static final long serialVersionUID = 5781456051409189118L;

    /**
     * Create an instance of {@link SourceReferenceColumnMapping}.
     */
    public SourceReferenceColumnMapping() {
        this(builder().exasolColumnName(DEFAULT_COLUMN_NAME));
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
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    @Override
    public void accept(final ColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "SourceReferenceColumnMapping(super=" + super.toString() + ")";
    }

    @Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SourceReferenceColumnMapping)) {
            return false;
        }
        if (!(this instanceof SourceReferenceColumnMapping)) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
