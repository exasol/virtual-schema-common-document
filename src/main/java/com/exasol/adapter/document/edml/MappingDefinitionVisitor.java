package com.exasol.adapter.document.edml;

/**
 * Visitor for {@link MappingDefinition}s.
 */
public interface MappingDefinitionVisitor {

    /**
     * Visit {@link Fields}.
     * 
     * @param fields object to visit
     */
    void visit(Fields fields);

    /**
     * Visit {@link ToDecimalMapping}.
     * 
     * @param toDecimalMapping object to visit
     */
    void visit(ToDecimalMapping toDecimalMapping);

    /**
     * Visit {@link ToJsonMapping}.
     * 
     * @param toJsonMapping object to visit
     */
    void visit(ToJsonMapping toJsonMapping);

    /**
     * Visit {@link ToTableMapping}.
     * 
     * @param toTableMapping object to visit
     */
    void visit(ToTableMapping toTableMapping);

    /**
     * Visit {@link ToVarcharMapping}.
     * 
     * @param toVarcharMapping object to visit
     */
    void visit(ToVarcharMapping toVarcharMapping);

    /**
     * Visit {@link ToDoubleMapping}.
     *
     * @param toDoubleMapping object to visit
     */
    void visit(ToDoubleMapping toDoubleMapping);
}
