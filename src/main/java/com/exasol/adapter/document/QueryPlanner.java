package com.exasol.adapter.document;

import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;

/**
 * This class plans the query execution and returns it as {@link QueryPlan}.
 * <p>
 * For that it builds one ore more {@link DataLoader}s that fetch the required documents for a given
 * {@link RemoteTableQuery}. If multiple loaders are returned, then the results must be combined by an
 * {@code UNION ALL}. This combination is implicitly implemented by the UDFs as multiple UDFs emit the value. This
 * results in an union of the values without duplicate elimination.
 * </p>
 * <p>
 * In addition this class builds a post selection that will be applied to the result of the {@link DataLoader}s.
 * </p>
 */
public interface QueryPlanner {
    /**
     * Builds a {@link DocumentFetcher} for a given query
     *
     * @param remoteTableQuery            the document query build the {@link DocumentFetcher} for
     * @param maxNumberOfParallelFetchers the maximum amount of {@link DocumentFetcher}s that can be used in parallel
     * @return {@link QueryPlan} generated plan
     */
    public QueryPlan planQuery(final RemoteTableQuery remoteTableQuery, int maxNumberOfParallelFetchers);
}
