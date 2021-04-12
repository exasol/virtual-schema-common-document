package com.exasol.adapter.document.queryplan;

import java.util.List;

import com.exasol.adapter.document.UdfCallBuilder;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.querypredicate.QueryPredicate;

/**
 * This class describes a non-empty {@link QueryPlan} for execution of a query.
 */
public class FetchQueryPlan implements QueryPlan {
    private final List<DocumentFetcher> documentFetcher;
    private final QueryPredicate postSelection;

    /**
     * Create a new instance of {@link FetchQueryPlan}.
     * 
     * @param documentFetcher {@link DocumentFetcher}s that will be executed as UDFs
     * @param postSelection   post selection that will be added to the push down SQL by the {@link UdfCallBuilder}.
     */
    public FetchQueryPlan(final List<DocumentFetcher> documentFetcher, final QueryPredicate postSelection) {
        this.documentFetcher = documentFetcher;
        this.postSelection = postSelection;
    }

    /**
     * Get the {@link DocumentFetcher}s.
     * 
     * @return {@link DocumentFetcher}s that will be executed as UDFs
     */
    public List<DocumentFetcher> getDocumentFetcher() {
        return this.documentFetcher;
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
