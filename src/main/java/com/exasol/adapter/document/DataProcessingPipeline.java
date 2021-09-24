package com.exasol.adapter.document;

import java.util.Iterator;
import java.util.List;

import com.exasol.ExaConnectionInformation;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.mapping.SchemaMapper;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;

/**
 * This class implements the data processing in the UDF.
 */
public class DataProcessingPipeline {
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
     * @throws InterruptedException if interrupted during execution
     */
    public void run(final DocumentFetcher documentFetcher, final ExaConnectionInformation connectionInformation,
            final RowHandler rowHandler) throws InterruptedException {
        final Iterator<FetchedDocument> documentIterator = documentFetcher.run(connectionInformation);
        while (documentIterator.hasNext()) {
            this.schemaMapper.mapRow(documentIterator.next(), rowHandler::acceptRow);
        }
    }

    @FunctionalInterface
    public interface RowHandler {
        void acceptRow(List<Object> row);
    }
}
