package com.exasol.adapter.document;

import java.util.List;
import java.util.logging.Logger;

import com.exasol.adapter.document.connection.ConnectionPropertiesReader;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.iterators.CloseableIterator;
import com.exasol.adapter.document.mapping.SchemaMapper;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;

/**
 * This class implements the data processing in the UDF.
 */
public class DataProcessingPipeline {
    private static final Logger LOG = Logger.getLogger(DataProcessingPipeline.class.getName());
    private final SchemaMapper schemaMapper;

    /**
     * Create a new instance of {@link DataProcessingPipeline}.
     * 
     * @param schemaMappingRequest schema mapping request
     */
    public DataProcessingPipeline(final SchemaMappingRequest schemaMappingRequest) {
        this.schemaMapper = new SchemaMapper(schemaMappingRequest);
    }

    /**
     * Run the data processing.
     * 
     * @param documentFetcher       document fetcher
     * @param connectionInformation connection information
     * @param rowHandler            handler for the read rows
     */
    public void run(final DocumentFetcher documentFetcher, final ConnectionPropertiesReader connectionInformation,
            final RowHandler rowHandler) {
        LOG.info(() -> "Start processing using document fetcher " + documentFetcher.getClass().getName());
        int rowCount = 0;
        try (final CloseableIterator<FetchedDocument> documentIterator = documentFetcher.run(connectionInformation)) {
            while (documentIterator.hasNext()) {
                this.schemaMapper.mapRow(documentIterator.next(), rowHandler::acceptRow);
                rowCount++;
            }
        }
        final int finalRowCount = rowCount;
        LOG.info(() -> "Read " + finalRowCount + " rows");
    }

    /**
     * Interface for classes that consume rows.
     */
    @FunctionalInterface
    public interface RowHandler {
        /**
         * Accept a row.
         * 
         * @param row row as list of standard Java objects like {@link String} for VARCHAR values
         */
        void acceptRow(List<Object> row);
    }
}
