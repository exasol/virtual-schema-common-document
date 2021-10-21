package com.exasol.adapter.document.mapping;

import java.io.Serializable;

import com.exasol.adapter.document.mapping.reader.JsonSchemaMappingReader;
import com.exasol.adapter.metadata.DataType;

/**
 * This interface defines the mapping for a column in the Virtual Schema.
 *
 * <p>
 * Objects implementing this interface get serialized into the column adapter notes. They are created using the
 * {@link JsonSchemaMappingReader}. Storing the mapping definition is necessary as mapping definition files in BucketFS
 * could be changed, but the mapping must not be changed until a {@code REFRESH} statement is called.
 * </p>
 */
public interface ColumnMapping extends Serializable {

    /**
     * Get the name of the column in the Exasol table.
     *
     * @return name of the column
     */
    String getExasolColumnName();

    /**
     * Get the Exasol data type.
     *
     * @return Exasol data type
     */
    DataType getExasolDataType();

    /**
     * Describes if Exasol column is nullable.
     *
     * @return {@code <true>} if Exasol column is nullable
     */
    boolean isExasolColumnNullable();

    /**
     * Create a copy of this column with a different name
     * 
     * @param newExasolName new name
     * @return copy
     */
    ColumnMapping withNewExasolName(String newExasolName);

    /**
     * Accept a {@link ColumnMappingVisitor}.
     * 
     * @param visitor visitor to accept
     */
    void accept(ColumnMappingVisitor visitor);
}
