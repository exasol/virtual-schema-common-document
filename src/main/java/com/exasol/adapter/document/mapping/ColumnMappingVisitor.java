package com.exasol.adapter.document.mapping;

/**
 * Visitor for {@link ColumnMapping}
 */
public interface ColumnMappingVisitor {
    /**
     * Visit a {@link PropertyToColumnMapping}.
     * 
     * @param propertyToColumnMapping {@link PropertyToColumnMapping} to visit
     */
    void visit(PropertyToColumnMapping propertyToColumnMapping);

    /**
     * Visit a {@link IterationIndexColumnMapping}.
     *
     * @param iterationIndexColumnDefinition {@link IterationIndexColumnMapping} to visit
     */
    void visit(IterationIndexColumnMapping iterationIndexColumnDefinition);

    /**
     * Visit a {@link SourceReferenceColumnMapping}.
     *
     * @param sourceReferenceColumnMapping {@link SourceReferenceColumnMapping} to visit
     */
    void visit(SourceReferenceColumnMapping sourceReferenceColumnMapping);
}
