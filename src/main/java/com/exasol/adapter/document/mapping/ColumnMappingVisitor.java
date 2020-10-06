package com.exasol.adapter.document.mapping;

/**
 * Visitor for {@link ColumnMapping}
 */
public interface ColumnMappingVisitor {
    void visit(PropertyToColumnMapping propertyToColumnMapping);
    void visit(IterationIndexColumnMapping iterationIndexColumnDefinition);

    void visit(SourceReferenceColumnMapping sourceReferenceColumnMapping);
}
