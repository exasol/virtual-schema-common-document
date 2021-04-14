package com.exasol.adapter.document;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.exasol.ExaConnectionInformation;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.mapping.SchemaMapper;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expresion.ValueExpressionToJavaObjectConverter;

import akka.actor.ActorSystem;
import akka.stream.Attributes;
import akka.stream.javadsl.Source;

/**
 * This class implements the data processing int the UDF.
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
        final ActorSystem system = ActorSystem.create("DataProcessingPipeline");
        try {
            Source.fromJavaStream(() -> documentFetcher.run(connectionInformation)).async()
                    .addAttributes(Attributes.inputBuffer(4, 4))
                    .flatMapConcat(row -> Source.fromJavaStream(() -> this.schemaMapper.mapRow(row)))
                    .map(row -> row.stream().map(this.valueExpressionToJavaObjectConverter::convert)
                            .collect(Collectors.toList()))
                    .async().addAttributes(Attributes.inputBuffer(4, 4)).runForeach(rowHandler::acceptRow, system)
                    .toCompletableFuture().get();
        } catch (final ExecutionException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-VSD-48").message("Failed to execute document pipeline.").toString(),
                    exception);
        }
    }

    @FunctionalInterface
    public interface RowHandler {
        void acceptRow(List<Object> row);
    }
}
