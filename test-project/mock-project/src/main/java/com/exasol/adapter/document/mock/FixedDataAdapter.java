package com.exasol.adapter.document.mock;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import com.exasol.ExaConnectionInformation;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.capabilities.Capabilities;
import com.exasol.adapter.document.DocumentAdapter;
import com.exasol.adapter.document.QueryPlanner;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.adapter.document.iterators.CloseableIterator;
import com.exasol.adapter.document.iterators.CloseableIteratorWrapper;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
import com.exasol.adapter.document.queryplan.FetchQueryPlan;
import com.exasol.adapter.document.queryplan.QueryPlan;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.document.querypredicate.NoPredicate;

/**
 * This class is a mock implementation of the interface that the virtual-schema-common-document defines.
 * <p>
 * We need this mock for testing the virtual-schema-common-document implementation.
 * </p>
 * <p>
 * This adapter always returns the same hardcoded data.
 * </p>
 */
public class FixedDataAdapter extends DocumentAdapter {
    static final String ADAPTER_NAME = "FIXED_DATA_ADAPTER";

    @Override
    protected TableKeyFetcher getTableKeyFetcher(final ExaConnectionInformation connectionInformation) {
        return (tableName, mappedColumns) -> Collections.emptyList();
    }

    @Override
    protected QueryPlanner getQueryPlanner(final ExaConnectionInformation connectionInformation,
            final AdapterProperties adapterProperties) {
        return new QueryPlannerStub();
    }

    @Override
    protected String getAdapterName() {
        return ADAPTER_NAME;
    }

    @Override
    protected Capabilities getCapabilities() {
        return Capabilities.builder().build();
    }

    private static class QueryPlannerStub implements QueryPlanner {

        @Override
        public QueryPlan planQuery(final RemoteTableQuery remoteTableQuery, final int maxNumberOfParallelFetchers) {
            return new FetchQueryPlan(List.of(new StaticDocumentFetcher()), new NoPredicate());
        }
    }

    private static class StaticDocumentFetcher implements DocumentFetcher {
        private static final long serialVersionUID = 7502407909281382595L;
        private static final ObjectHolderNode STATIC_VALUE = new ObjectHolderNode(
                Map.of("isbn", new StringHolderNode("123456789"), //
                        "name", new StringHolderNode("Tom Sawyer"), //
                        "publication_date", new DateHolderNode(new Date(1632297287000L)), //
                        "my_timestamp", new TimestampHolderNode(new Timestamp(1632297287000L))));

        @Override
        public CloseableIterator<FetchedDocument> run(final ExaConnectionInformation connectionInformation) {
            return new CloseableIteratorWrapper<>(
                    List.of(new FetchedDocument(STATIC_VALUE, "staticFromTestCode")).iterator());
        }
    }
}
