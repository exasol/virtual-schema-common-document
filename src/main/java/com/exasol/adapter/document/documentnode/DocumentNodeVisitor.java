package com.exasol.adapter.document.documentnode;

/**
 * Visitor for the {@link DocumentNode} structure.
 */
public interface DocumentNodeVisitor {

    /**
     * Visit a {@link DocumentArray}.
     * 
     * @param array array to visit
     */
    public void visit(DocumentArray array);

    /**
     * Visit a {@link DocumentObject}.
     * 
     * @param object object to visit
     */
    public void visit(DocumentObject object);

    /**
     * Visit a {@link DocumentNullValue}.
     * 
     * @param nullValue null value to visit
     */
    public void visit(DocumentNullValue nullValue);

    /**
     * Visit a {@link DocumentStringValue}.
     * 
     * @param stringValue string value to visit
     */
    public void visit(DocumentStringValue stringValue);

    /**
     * Visit a {@link DocumentBigDecimalValue}.
     * 
     * @param stringValue big decimal value to visit
     */
    public void visit(DocumentBigDecimalValue stringValue);

    /**
     * Visit a {@link DocumentBooleanValue}.
     * 
     * @param booleanValue boolean value to visit
     */
    public void visit(DocumentBooleanValue booleanValue);
}
