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
     * Visit a {@link DocumentDecimalValue}.
     * 
     * @param bigDecimalValue big decimal value to visit
     */
    public void visit(DocumentDecimalValue bigDecimalValue);

    /**
     * Visit a {@link DocumentBooleanValue}.
     * 
     * @param booleanValue boolean value to visit
     */
    public void visit(DocumentBooleanValue booleanValue);

    /**
     * Visit a {@link DocumentFloatingPointValue}.
     *
     * @param floatingPointValue double value to visit
     */
    public void visit(DocumentFloatingPointValue floatingPointValue);

    /**
     * Visit a {@link DocumentBinaryValue}.
     *
     * @param binaryValue binary data
     */
    public void visit(DocumentBinaryValue binaryValue);
}
