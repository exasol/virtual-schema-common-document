package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;

/**
 * {@link ColumnValueExtractor} for {@link SourceReferenceColumnMapping}s.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class SourceReferenceColumnValueExtractor implements ColumnValueExtractor {

    @Override
    public Object extractColumnValue(final FetchedDocument document,
            final PathIterationStateProvider arrayAllIterationState) {
        return document.getSourcePath();
    }
}
