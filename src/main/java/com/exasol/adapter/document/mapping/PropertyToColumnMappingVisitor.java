package com.exasol.adapter.document.mapping;

/**
 * Visitor for {@link PropertyToColumnMapping}.
 */
public interface PropertyToColumnMappingVisitor {
    /**
     * Visits a {@link PropertyToVarcharColumnMapping}.
     * 
     * @param columnDefinition {@link PropertyToVarcharColumnMapping} to visit
     */
    void visit(PropertyToVarcharColumnMapping columnDefinition);

    /**
     * Visits a {@link PropertyToJsonColumnMapping}.
     * 
     * @param columnDefinition {@link PropertyToJsonColumnMapping} to visit
     */
    void visit(PropertyToJsonColumnMapping columnDefinition);

    /**
     * Visits a {@link PropertyToDecimalColumnMapping}.
     *
     * @param columnDefinition {@link PropertyToDecimalColumnMapping} to visit
     */
    void visit(PropertyToDecimalColumnMapping columnDefinition);
}
