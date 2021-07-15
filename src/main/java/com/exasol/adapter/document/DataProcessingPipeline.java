package com.exasol.adapter.document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.exasol.ExaConnectionInformation;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.mapping.SchemaMapper;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expresion.ValueExpressionToJavaObjectConverter;
import com.exasol.sql.expression.ValueExpression;

import akka.actor.ActorSystem;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;

/**
 * This class implements the data processing in the UDF.
 */
public class DataProcessingPipeline {
    private final ValueExpressionToJavaObjectConverter valueExpressionToJavaObjectConverter;
    private final SchemaMapper schemaMapper;
    private final DataProcessingPipelineMonitor dataProcessingPipelineMonitor;

    /**
     * Create a new instance of {@link DataProcessingPipeline}.
     * 
     * @param schemaMappingRequest schema mapping request
     */
    public DataProcessingPipeline(final SchemaMappingRequest schemaMappingRequest) {
        this.valueExpressionToJavaObjectConverter = new ValueExpressionToJavaObjectConverter();
        this.schemaMapper = new SchemaMapper(schemaMappingRequest);
        this.dataProcessingPipelineMonitor = new DataProcessingPipelineMonitor();
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
        this.dataProcessingPipelineMonitor.start();
        try {
            Source.fromIterator(() -> documentFetcher.run(connectionInformation)).async()//
                    .map(this.dataProcessingPipelineMonitor::onEnterPreSchemaMappingBuffer)//
                    .buffer(600, OverflowStrategy.backpressure())//
                    .map(this::runSchemaMapping).async()//
                    .buffer(600, OverflowStrategy.backpressure())//
                    .runForeach(chunk -> emitChunk(chunk, rowHandler), system).exceptionally(exception -> {
                        throw new IllegalStateException(exception);
                    }).toCompletableFuture().get();
        } catch (final ExecutionException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-VSD-48").message("Failed to execute document pipeline.").toString(),
                    exception);
        } finally {
            this.dataProcessingPipelineMonitor.requestStop();
            system.terminate();
        }
    }

    private List<List<Object>> runSchemaMapping(final List<FetchedDocument> group) {
        this.dataProcessingPipelineMonitor.onLeavePreSchemaMappingBuffer();
        final List<List<Object>> result = new ArrayList<>();
        for (final FetchedDocument document : group) {
            this.schemaMapper.mapRow(document, row -> result.add(convertRowToJavaObjects(row)));
        }
        this.dataProcessingPipelineMonitor.onEnterPreEmitBuffer();
        return result;
    }

    private List<Object> convertRowToJavaObjects(final List<ValueExpression> row) {
        final List<Object> result = new ArrayList<>(row.size());
        for (final ValueExpression item : row) {
            result.add(this.valueExpressionToJavaObjectConverter.convert(item));
        }
        return result;
    }

    private void emitChunk(final List<List<Object>> chunkOfRows, final RowHandler rowHandler) {
        this.dataProcessingPipelineMonitor.onLeavePreEmitBuffer(chunkOfRows.size());
        for (final List<Object> row : chunkOfRows) {
            rowHandler.acceptRow(row);
        }
    }

    @FunctionalInterface
    public interface RowHandler {
        void acceptRow(List<Object> row);
    }
}
