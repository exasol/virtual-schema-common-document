package com.exasol.adapter.document;

/**
 * Interface for {@link DocumentAdapter}s that create an {@link DataLoaderUdf}.
 */
public interface DataLoaderUdfFactory {
    /**
     * Factory method for DynamoDB specific {@link DataLoaderUdf}.
     * 
     * @return created {@link DataLoaderUdf}
     */
    DataLoaderUdf getDataLoaderUDF();
}
