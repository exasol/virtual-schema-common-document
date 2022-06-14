package com.exasol.adapter.document.documentfetcher;

import java.io.Serializable;

import com.exasol.adapter.document.connection.ConnectionPropertiesReader;
import com.exasol.adapter.document.iterators.CloseableIterator;

/**
 * This interface fetches document data from a remote database.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public interface DocumentFetcher extends Serializable {
    /**
     * Executes the planned operation.
     *
     * @param connectionPropertiesReader for reading connection details for a connection to the remote database
     * @return result of the operation.
     */
    public CloseableIterator<FetchedDocument> run(final ConnectionPropertiesReader connectionPropertiesReader);
}
