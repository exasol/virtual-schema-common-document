package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This class describes a column that maps the source reference. For example in the files-virtual-schemas the name of
 * the file that contained the document.
 * <p>
 * The name of this column is always {@code SOURCE_REFERENCE}, except from if it is used as foreign key (not yet
 * possible).
 * </p>
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public final class SourceReferenceColumnMapping extends AbstractColumnMapping {
    public static final String DEFAULT_COLUMN_NAME = "SOURCE_REFERENCE";
    private static final long serialVersionUID = -5340069183615064215L;//

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
}
