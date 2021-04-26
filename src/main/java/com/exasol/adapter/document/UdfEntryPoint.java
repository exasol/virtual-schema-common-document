package com.exasol.adapter.document;

import java.io.IOException;
import java.util.List;

import com.exasol.*;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;
import com.exasol.errorreporting.ExaError;
import com.exasol.utils.StringSerializer;

/**
 * Main UDF entry point.
 */
public class UdfEntryPoint {
    public static final String UDF_PREFIX = "IMPORT_FROM_";
    public static final String PARAMETER_DOCUMENT_FETCHER = "DATA_LOADER";
    public static final String PARAMETER_SCHEMA_MAPPING_REQUEST = "SCHEMA_MAPPING_REQUEST";
    public static final String PARAMETER_CONNECTION_NAME = "CONNECTION_NAME";

    private UdfEntryPoint() {
        // Intentionally empty. As this class is only accessed statical.
    }

    /**
     * This method is called by the Exasol database when the ImportFromDynamodb UDF is called.
     *
     * @param exaMetadata exasol metadata
     * @param exaIterator iterator
     * @throws Exception if data can't get loaded
     */
    @SuppressWarnings("java:S112") // Exception is too generic. This signature is however given by the UDF framework
    public static void run(final ExaMetadata exaMetadata, final ExaIterator exaIterator) throws Exception {
        final ExaConnectionInformation connectionInformation = exaMetadata
                .getConnection(exaIterator.getString(PARAMETER_CONNECTION_NAME));
        final SchemaMappingRequest schemaMappingRequest = deserializeSchemaMappingRequest(exaIterator);
        do {
            final DocumentFetcher documentFetcher = deserializeDocumentFetcher(exaIterator);
            new DataProcessingPipeline(schemaMappingRequest).run(documentFetcher, connectionInformation,
                    row -> emitRow(row, exaIterator));
        } while (exaIterator.next());
    }

    private static DocumentFetcher deserializeDocumentFetcher(final ExaIterator exaIterator)
            throws ExaIterationException, ExaDataTypeException, IOException, ClassNotFoundException {
        final String serialized = exaIterator.getString(PARAMETER_DOCUMENT_FETCHER);
        return (DocumentFetcher) StringSerializer.deserializeFromString(serialized);
    }

    private static SchemaMappingRequest deserializeSchemaMappingRequest(final ExaIterator exaIterator)
            throws ExaIterationException, ExaDataTypeException, IOException, ClassNotFoundException {
        final String serialized = exaIterator.getString(PARAMETER_SCHEMA_MAPPING_REQUEST);
        return (SchemaMappingRequest) StringSerializer.deserializeFromString(serialized);
    }

    private static void emitRow(final List<Object> row, final ExaIterator iterator) {
        try {
            iterator.emit(row.toArray());
        } catch (final ExaIterationException | ExaDataTypeException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-68")
                    .message("An error occurred during processing the UDF call.").ticketMitigation().toString(),
                    exception);
        }
    }
}
