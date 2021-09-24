package com.exasol.adapter.document.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentpath.DocumentPathIteratorFactory;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;

/**
 * This class extracts Exasol the column values from document data.
 */
public class SchemaMapper {
    private final SchemaMappingRequest request;
    private final ColumnValueExtractorFactory columnValueExtractorFactory;

    /**
     * Create a new {@link SchemaMapper} for the given query.
     *
     * @param request request for the schema mapping
     */
    public SchemaMapper(final SchemaMappingRequest request) {
        this.request = request;
        this.columnValueExtractorFactory = new ColumnValueExtractorFactory();
    }

    /**
     * Processes a document according to the given schema definition and gives an Exasol result row. If a non-root table
     * is queried multiple results can be returned.
     *
     * @param document       document to map
     * @param resultConsumer function that consumes the rows
     */
    public void mapRow(final FetchedDocument document, final Consumer<List<Object>> resultConsumer) {
        final DocumentPathIteratorFactory arrayAllCombinationIterable = new DocumentPathIteratorFactory(
                this.request.getPathInRemoteTable(), document.getRootDocumentNode());
        arrayAllCombinationIterable
                .forEach(iterationState -> resultConsumer.accept(mapColumns(document, iterationState)));
    }

    private List<Object> mapColumns(final FetchedDocument document,
            final PathIterationStateProvider arrayAllIterationState) {
        final List<Object> resultValues = new ArrayList<>(this.request.getColumns().size());
        for (final ColumnMapping resultColumn : this.request.getColumns()) {
            final ColumnValueExtractor columnValueExtractor = this.columnValueExtractorFactory
                    .getValueExtractorForColumn(resultColumn);
            final Object result = columnValueExtractor.extractColumnValue(document, arrayAllIterationState);
            resultValues.add(result);
        }
        return resultValues;
    }
}
