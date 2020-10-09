package com.exasol.adapter.document.mapping;

import java.io.Serializable;
import java.util.List;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;

/**
 * This class describes the request for the schema mapping.
 */
public class SchemaMappingRequest implements Serializable {
    private static final long serialVersionUID = -1681839026887057101L;
    /** @serial */
    private final DocumentPathExpression pathInRemoteTable;
    /** @serial */
    private final List<ColumnMapping> columns;

    /**
     * Create a new instance of {@link SchemaMappingRequest}.
     * 
     * @param pathInRemoteTable path to root of the document to extract
     * @param columns           columns to extract
     */
    public SchemaMappingRequest(final DocumentPathExpression pathInRemoteTable, final List<ColumnMapping> columns) {
        this.pathInRemoteTable = pathInRemoteTable;
        this.columns = columns;
    }

    /**
     * Get the path to root of the document to extract
     * 
     * @return path to root of the document to extract
     */
    public DocumentPathExpression getPathInRemoteTable() {
        return this.pathInRemoteTable;
    }

    /**
     * Get the columns to map.
     * 
     * @return columns to map
     */
    public List<ColumnMapping> getColumns() {
        return this.columns;
    }
}
