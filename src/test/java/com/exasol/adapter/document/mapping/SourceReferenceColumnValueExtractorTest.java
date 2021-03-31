package com.exasol.adapter.document.mapping;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;

class SourceReferenceColumnValueExtractorTest {
    private static final SourceReferenceColumnValueExtractor<Object> EXTRACTOR = new SourceReferenceColumnValueExtractor<>();

    @Test
    void test() {
        final String sourcePath = "test source";
        final FetchedDocument document = new FetchedDocument(null, sourcePath);
        assertThat(EXTRACTOR.extractColumnValue(document, null).toString(), equalTo(sourcePath));
    }
}