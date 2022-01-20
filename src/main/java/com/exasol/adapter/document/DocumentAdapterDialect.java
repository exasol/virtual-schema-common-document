package com.exasol.adapter.document;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.capabilities.Capabilities;
import com.exasol.adapter.document.connection.ConnectionPropertiesReader;
import com.exasol.adapter.document.mapping.TableKeyFetcher;

/**
 * Interface for document dialects.
 */
public interface DocumentAdapterDialect {

    /**
     * Get a database specific {@link TableKeyFetcher}.
     *
     * @param connectionInformation connection details
     * @return database specific {@link TableKeyFetcher}
     */
    public TableKeyFetcher getTableKeyFetcher(final ConnectionPropertiesReader connectionInformation);

    /**
     * Get a data source specific {@link QueryPlanner}.
     *
     * @param connectionInformation connection details
     * @param adapterProperties     adapter properties
     * @return source specific {@link QueryPlanner}
     */
    public QueryPlanner getQueryPlanner(final ConnectionPropertiesReader connectionInformation,
            final AdapterProperties adapterProperties);

    /**
     * Get the name of the database-specific adapter.
     *
     * @return name of the database-specific adapter
     */
    public String getAdapterName();

    /**
     * Get the capabilities of the dialect.
     *
     * @return capabilities
     */
    public Capabilities getCapabilities();

    /**
     * Get the URL of the dialect specific user guide.
     *
     * @return user guide URL
     */
    public String getUserGuideUrl();
}
