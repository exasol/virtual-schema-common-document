package com.exasol.adapter.document;

import java.util.List;

import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;

/**
 * This factory builds {@link DataLoader}s that fetch the required documents for a given {@link RemoteTableQuery}. If
 * multiple loaders are returned, then the results must be combined by an {@code UNION ALL}. This combination is
 * implicitly implemented by the UDFs as multiple UDFs emit the value. This results in an union of the values without
 * duplicate elimination.
 */
public interface DataLoaderFactory {
    /**
     * Builds a {@link DocumentFetcher} for a given query
     *
     * @param remoteTableQuery            the document query build the {@link DocumentFetcher} for
     * @param maxNumberOfParallelFetchers the maximum amount of {@link DocumentFetcher}s that can be used in parallel
     * @return {@link DocumentFetcher}
     */
    List<DataLoader> buildDataLoaderForQuery(final RemoteTableQuery remoteTableQuery, int maxNumberOfParallelFetchers);
}
