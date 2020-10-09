package com.exasol.adapter.document;

import java.util.List;

import com.exasol.adapter.document.querypredicate.QueryPredicate;

/**
 * This class represents a plan for executing the query. It is built by {@link QueryPlanner}s.
 */
public class QueryPlan {
    private final List<DataLoader> dataLoaders;
    private final QueryPredicate postSelection;

    /**
     * Create a new instance of {@link QueryPlan}.
     * 
     * @param dataLoaders   {@link DataLoader}s that will be executed as UDFs
     * @param postSelection post selection that will be added to the push down SQL by the {@link UdfCallBuilder}.
     */
    public QueryPlan(final List<DataLoader> dataLoaders, final QueryPredicate postSelection) {
        this.dataLoaders = dataLoaders;
        this.postSelection = postSelection;
    }

    /**
     * Get the {@link DataLoader}s.
     * 
     * @return {@link DataLoader}s that will be executed as UDFs
     */
    public List<DataLoader> getDataLoaders() {
        return this.dataLoaders;
    }

    /**
     * Get the post selection.
     * 
     * @return post selection that will be added to the push down SQL by the {@link UdfCallBuilder}
     */
    public QueryPredicate getPostSelection() {
        return this.postSelection;
    }
}
