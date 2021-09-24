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

    /**
     * Visits a {@link PropertyToDoubleColumnMapping}.
     *
     * @param columnDefinition {@link PropertyToDoubleColumnMapping} to visit
     */
    void visit(PropertyToDoubleColumnMapping columnDefinition);

    /**
     * Visits a {@link PropertyToBoolColumnMapping}.
     *
     * @param columnDefinition {@link PropertyToBoolColumnMapping} to visit
     */
    void visit(PropertyToBoolColumnMapping columnDefinition);

    /**
     * Visits a {@link PropertyToDateColumnMapping}.
     *
     * @param columnDefinition {@link PropertyToDateColumnMapping} to visit
     */
    void visit(PropertyToDateColumnMapping columnDefinition);

    /**
     * Visits a {@link PropertyToTimestampColumnMapping}.
     *
     * @param columnDefinition {@link PropertyToTimestampColumnMapping} to visit
     */
    void visit(PropertyToTimestampColumnMapping columnDefinition);
}
