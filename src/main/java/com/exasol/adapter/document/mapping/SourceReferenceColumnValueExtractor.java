package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.StringLiteral;

/**
 * {@link ColumnValueExtractor} for {@link SourceReferenceColumnMapping}s.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class SourceReferenceColumnValueExtractor implements ColumnValueExtractor {

    @Override
    public ValueExpression extractColumnValue(final FetchedDocument document,
            final PathIterationStateProvider arrayAllIterationState) {
        return StringLiteral.of(document.getSourcePath());
    }
}
