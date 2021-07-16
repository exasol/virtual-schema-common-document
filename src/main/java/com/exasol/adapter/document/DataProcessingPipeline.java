package com.exasol.adapter.document;

import java.util.*;

import com.exasol.ExaConnectionInformation;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.mapping.SchemaMapper;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;
import com.exasol.sql.expresion.ValueExpressionToJavaObjectConverter;
import com.exasol.sql.expression.ValueExpression;

/**
 * This class implements the data processing in the UDF.
 */
public class DataProcessingPipeline {
    private final ValueExpressionToJavaObjectConverter valueExpressionToJavaObjectConverter;
    private final SchemaMapper schemaMapper;

    /**
     * Create a new instance of {@link DataProcessingPipeline}.
     * 
     * @param schemaMappingRequest schema mapping request
     */
    public DataProcessingPipeline(final SchemaMappingRequest schemaMappingRequest) {
        this.valueExpressionToJavaObjectConverter = new ValueExpressionToJavaObjectConverter();
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
            this.schemaMapper.mapRow(documentIterator.next(),
                    row -> rowHandler.acceptRow(convertRowToJavaObjects(row)));
        }
    }

    private List<Object> convertRowToJavaObjects(final List<ValueExpression> row) {
        final List<Object> result = new ArrayList<>(row.size());
        for (final ValueExpression item : row) {
            result.add(this.valueExpressionToJavaObjectConverter.convert(item));
        }
        return result;
    }

    @FunctionalInterface
    public interface RowHandler {
        void acceptRow(List<Object> row);
    }
}
