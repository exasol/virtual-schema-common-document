package com.exasol.adapter.document.documentfetcher;

import java.io.Serializable;
import java.util.List;

import com.exasol.ExaConnectionInformation;

import akka.NotUsed;
import akka.stream.javadsl.Source;

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
    Source<List<FetchedDocument>, NotUsed> run(ExaConnectionInformation connectionInformation);
}
