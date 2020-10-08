package com.exasol.adapter.document.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentpath.DocumentPathIteratorFactory;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;
import com.exasol.sql.expression.ValueExpression;

/**
 * This class extracts Exasol the column values from document data.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class SchemaMapper<DocumentVisitorType> {
    private final SchemaMappingRequest request;
    private final ColumnValueExtractorFactory<DocumentVisitorType> columnValueExtractorFactory;

    /**
     * Create a new {@link SchemaMapper} for the given query.
     *
     * @param request                               request for the schema mapping
     * @param propertyToColumnValueExtractorFactory factory for value mapper corresponding to
     *                                              {@link DocumentVisitorType}
     */
    public SchemaMapper(final SchemaMappingRequest request,
            final PropertyToColumnValueExtractorFactory<DocumentVisitorType> propertyToColumnValueExtractorFactory) {
        this.request = request;
        this.columnValueExtractorFactory = new ColumnValueExtractorFactory<>(propertyToColumnValueExtractorFactory);
    }

    /**
     * Processes a document according to the given schema definition and gives an Exasol result row. If a non-root table
     * is queried multiple results can be returned.
     *
     * @param document document to map
     * @return stream of exasol rows
     */
    public Stream<List<ValueExpression>> mapRow(final FetchedDocument<DocumentVisitorType> document) {
        final DocumentPathIteratorFactory<DocumentVisitorType> arrayAllCombinationIterable = new DocumentPathIteratorFactory<>(
                this.request.getPathInRemoteTable(), document.getRootDocumentNode());
        return arrayAllCombinationIterable.stream().map(iterationState -> mapColumns(document, iterationState));
    }

    private List<ValueExpression> mapColumns(final FetchedDocument<DocumentVisitorType> document,
            final PathIterationStateProvider arrayAllIterationState) {
        final List<ValueExpression> resultValues = new ArrayList<>(this.request.getColumns().size());
        for (final ColumnMapping resultColumn : this.request.getColumns()) {
            final ColumnValueExtractor<DocumentVisitorType> columnValueExtractor = this.columnValueExtractorFactory
                    .getValueExtractorForColumn(resultColumn);
            final ValueExpression result = columnValueExtractor.extractColumnValue(document, arrayAllIterationState);
            resultValues.add(result);
        }
        return resultValues;
    }
}
