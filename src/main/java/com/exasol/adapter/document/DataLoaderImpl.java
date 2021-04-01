package com.exasol.adapter.document;

import java.util.List;
import java.util.stream.Stream;

import com.exasol.ExaConnectionInformation;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.mapping.SchemaMapper;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;
import com.exasol.sql.expression.ValueExpression;

/**
 * Implementation of the {@link DataLoader} interface.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class DataLoaderImpl implements DataLoader {
    private static final long serialVersionUID = 4631968154506118888L;
    /** @serial */
    private final DocumentFetcher documentFetcher;

    /**
     * Create a new instance of {@link DataLoaderImpl}.
     * 
     * @param documentFetcher document fetcher that provides the document data.
     */
    public DataLoaderImpl(final DocumentFetcher documentFetcher) {
        this.documentFetcher = documentFetcher;
    }

    @Override
    public final Stream<List<ValueExpression>> run(final ExaConnectionInformation connectionInformation,
            final SchemaMappingRequest schemaMappingRequest) {
        final SchemaMapper schemaMapper = new SchemaMapper(schemaMappingRequest);
        return this.documentFetcher.run(connectionInformation).flatMap(schemaMapper::mapRow);
    }
}
