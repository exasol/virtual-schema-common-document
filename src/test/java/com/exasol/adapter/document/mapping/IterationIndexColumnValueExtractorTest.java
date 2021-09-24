package com.exasol.adapter.document.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentnode.holder.StringHolderNode;
import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;

class IterationIndexColumnValueExtractorTest {
    private static final DocumentPathExpression TABLES_PATH = DocumentPathExpression.builder().addObjectLookup("test")
            .addArrayAll().build();
    private static final IterationIndexColumnMapping COLUMN = new IterationIndexColumnMapping("INDEX", TABLES_PATH);
    private static final IterationIndexColumnValueExtractor EXTRACTOR = new IterationIndexColumnValueExtractor(COLUMN);
    private static final int ITERATION_INDEX = 14;
    PathIterationStateProvider ITERATION_STATE_PROVIDER = new PathIterationStateProvider() {
        @Override
        public int getIndexFor(final DocumentPathExpression pathToArrayAll) {
            if (pathToArrayAll.equals(TABLES_PATH)) {
                return ITERATION_INDEX;
            }
            return -1;
        }
    };

    @Test
    void testExtractColumnValue() {
        final BigDecimal intValue = (BigDecimal) EXTRACTOR
                .extractColumnValue(new FetchedDocument(new StringHolderNode(""), ""), this.ITERATION_STATE_PROVIDER);
        assertThat(intValue.longValue(), equalTo((long) ITERATION_INDEX));
    }
}