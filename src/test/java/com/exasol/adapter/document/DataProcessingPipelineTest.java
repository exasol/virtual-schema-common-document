package com.exasol.adapter.document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.connection.ConnectionPropertiesReader;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentnode.holder.BigDecimalHolderNode;
import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.adapter.document.iterators.CloseableIterator;
import com.exasol.adapter.document.iterators.CloseableIteratorWrapper;
import com.exasol.adapter.document.mapping.PropertyToColumnMapping;
import com.exasol.adapter.document.mapping.PropertyToDecimalColumnMapping;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;

class DataProcessingPipelineTest {
    private static final int TEST_SIZE = 100000;

    @Test
    void testResultIsCorrect() throws InterruptedException {
        final DataProcessingPipeline pipeline = getPipeline();
        final ArrayList<List<Object>> result = new ArrayList<>();
        pipeline.run(new HelperDocumentFetcher(() -> {
        }), null, result::add);
        assertThat(result.size(), equalTo(TEST_SIZE));
        assertThat(result.get(result.size() - 1).get(0), equalTo(BigDecimal.valueOf(TEST_SIZE - 1)));
    }

    @Test
    void testAsyncProcessing() throws InterruptedException {
        final List<EVENT> events = new ArrayList<>(TEST_SIZE * 2);
        final DataProcessingPipeline pipeline = getPipeline();
        pipeline.run(new HelperDocumentFetcher(() -> events.add(EVENT.GENERATE)), null, row -> {
            events.add(EVENT.EMIT);
        });
        assertThat("First emit was too late.", events.indexOf(EVENT.EMIT), lessThan(TEST_SIZE / 4));
    };

    private DataProcessingPipeline getPipeline() {
        final PropertyToColumnMapping columnMapping = PropertyToDecimalColumnMapping.builder().exasolColumnName("TEST")
                .decimalPrecision(10).decimalScale(0).lookupFailBehaviour(MappingErrorBehaviour.ABORT)
                .notNumericBehaviour(ConvertableMappingErrorBehaviour.ABORT)
                .overflowBehaviour(MappingErrorBehaviour.ABORT)
                .pathToSourceProperty(DocumentPathExpression.builder().build()).build();
        final SchemaMappingRequest schemaMappingRequest = new SchemaMappingRequest(DocumentPathExpression.empty(),
                List.of(columnMapping));
        return new DataProcessingPipeline(schemaMappingRequest);
    }

    private enum EVENT {
        GENERATE, EMIT
    }

    private static class HelperIterator implements Iterator<FetchedDocument> {
        private final Runnable onNext;
        int counter = 0;

        private HelperIterator(final Runnable onNext) {
            this.onNext = onNext;
        }

        @Override
        public boolean hasNext() {
            return this.counter < TEST_SIZE;
        }

        @Override
        public FetchedDocument next() {
            final BigDecimalHolderNode document = new BigDecimalHolderNode(BigDecimal.valueOf(this.counter));
            this.onNext.run();
            this.counter++;
            return new FetchedDocument(document, "generated");
        }
    }

    private static class HelperDocumentFetcher implements DocumentFetcher {
        private static final long serialVersionUID = 3777440484106526521L;
        private final Runnable onNext;

        private HelperDocumentFetcher(final Runnable onNext) {
            this.onNext = onNext;
        }

        @Override
        public CloseableIterator<FetchedDocument> run(final ConnectionPropertiesReader connectionInformation) {
            return new CloseableIteratorWrapper<>(new HelperIterator(this.onNext));
        }
    }
}