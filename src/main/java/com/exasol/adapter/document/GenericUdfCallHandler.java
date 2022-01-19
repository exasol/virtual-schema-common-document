package com.exasol.adapter.document;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import com.exasol.*;
import com.exasol.adapter.document.connection.ConnectionPropertiesReader;
import com.exasol.adapter.document.connection.ConnectionStringReader;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;
import com.exasol.errorreporting.ExaError;
import com.exasol.utils.StringSerializer;

/**
 * Handler for UDF calls.
 */
public class GenericUdfCallHandler {
    /** Prefix of the UDF names of the document virtual schemas. */
    public static final String UDF_PREFIX = "IMPORT_FROM_";
    /**
     * UDF-Parameter name for the {@link DocumentFetcher}. It's not named {@code DOCUMENT_FETCHER} for not breaking the
     * public API.
     */
    public static final String PARAMETER_DOCUMENT_FETCHER = "DATA_LOADER";
    /**
     * UDF-Parameter name for the {@link SchemaMappingRequest}.
     */
    public static final String PARAMETER_SCHEMA_MAPPING_REQUEST = "SCHEMA_MAPPING_REQUEST";
    /**
     * UDF-Parameter name for the connection name.
     */
    public static final String PARAMETER_CONNECTION_NAME = "CONNECTION_NAME";
    private final String userGuideUrl;

    public GenericUdfCallHandler(final String userGuideUrl) {
        this.userGuideUrl = userGuideUrl;
    }

    private static DocumentFetcher deserializeDocumentFetcher(final ExaIterator exaIterator) {
        try {
            final String serialized = exaIterator.getString(PARAMETER_DOCUMENT_FETCHER);
            return (DocumentFetcher) StringSerializer.deserializeFromString(serialized);
        } catch (final ExaDataTypeException | ExaIterationException | IOException | ClassNotFoundException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-95")
                    .message("Failed to deserialize schema document fetcher.").ticketMitigation().toString(),
                    exception);
        }
    }

    private static SchemaMappingRequest deserializeSchemaMappingRequest(final ExaIterator exaIterator) {
        try {
            final String serialized = exaIterator.getString(PARAMETER_SCHEMA_MAPPING_REQUEST);
            return (SchemaMappingRequest) StringSerializer.deserializeFromString(serialized);
        } catch (final ExaDataTypeException | ExaIterationException | IOException | ClassNotFoundException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-84")
                    .message("Failed to deserialize schema mapping request.").ticketMitigation().toString(), exception);
        }
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

    /**
     * This method is called by the Exasol database when the ImportFromDynamodb UDF is called.
     *
     * @param exaMetadata exasol metadata
     * @param exaIterator iterator
     * @throws ExaIterationException
     * @throws
     */
    public void run(final ExaMetadata exaMetadata, final ExaIterator exaIterator) {
        /*
         * Set the timezone to UTC so that timestamps are converted using the UTC timezone. Default seems to be
         * Europe/Berlin.
         */
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        final ConnectionPropertiesReader connectionPropertyReader = getConnectionPropertyReader(exaMetadata,
                exaIterator);
        final SchemaMappingRequest schemaMappingRequest = deserializeSchemaMappingRequest(exaIterator);
        try {
            do {
                final DocumentFetcher documentFetcher = deserializeDocumentFetcher(exaIterator);
                new DataProcessingPipeline(schemaMappingRequest).run(documentFetcher, connectionPropertyReader,
                        row -> emitRow(row, exaIterator));
            } while (exaIterator.next());
        } catch (final ExaIterationException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-97")
                    .message("Error while reading UDF parameters from Exasol.").ticketMitigation().toString(),
                    exception);
        }
    }

    private ConnectionPropertiesReader getConnectionPropertyReader(final ExaMetadata exaMetadata,
            final ExaIterator exaIterator) {
        try {
            final ExaConnectionInformation connection = exaMetadata
                    .getConnection(exaIterator.getString(PARAMETER_CONNECTION_NAME));
            final String connectionString = new ConnectionStringReader(this.userGuideUrl).read(connection);
            return new ConnectionPropertiesReader(connectionString, this.userGuideUrl);
        } catch (final ExaConnectionAccessException | ExaIterationException | ExaDataTypeException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-96")
                    .message("Failed to get connection information.").ticketMitigation().toString(), exception);
        }

    }
}
