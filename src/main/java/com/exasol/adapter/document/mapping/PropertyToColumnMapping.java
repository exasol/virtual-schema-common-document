package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;

/**
 * This interface defines the mapping from a property in the remote document to an Exasol column.
 */
public interface PropertyToColumnMapping extends ColumnMapping {

    /**
     * Get the path to the property to extract.
     *
     * @return path to the property to extract
     */
    DocumentPathExpression getPathToSourceProperty();

    /**
     * Get the {@link MappingErrorBehaviour} used in case that the path does not exist in the document.
     *
     * @return {@link MappingErrorBehaviour}
     */
    MappingErrorBehaviour getLookupFailBehaviour();

    void accept(PropertyToColumnMappingVisitor visitor);

    @Override
    default void accept(final ColumnMappingVisitor visitor) {
        visitor.visit(this);
    }
}
