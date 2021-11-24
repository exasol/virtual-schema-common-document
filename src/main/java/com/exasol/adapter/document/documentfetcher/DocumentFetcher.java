package com.exasol.adapter.document.documentfetcher;

import java.io.Serializable;

import com.exasol.ExaConnectionInformation;
import com.exasol.adapter.document.iterators.CloseableIterator;

/**
 * This interface fetches document data from a remote database.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public interface DocumentFetcher extends Serializable {
    /**
     * Executes the planed operation.
     *
     * @param connectionInformation for creating a connection to the remote database
     * @return result of the operation.
     */
    CloseableIterator<FetchedDocument> run(ExaConnectionInformation connectionInformation);
}
