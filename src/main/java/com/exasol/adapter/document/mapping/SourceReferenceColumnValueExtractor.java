package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;
import com.exasol.sql.expression.StringLiteral;
import com.exasol.sql.expression.ValueExpression;

/**
 * {@link ColumnValueExtractor} for {@link SourceReferenceColumnMapping}s.
 * 
 * @param <DocumentVisitorType>
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class SourceReferenceColumnValueExtractor<DocumentVisitorType>
        implements ColumnValueExtractor<DocumentVisitorType> {

    @Override
    public ValueExpression extractColumnValue(final FetchedDocument<DocumentVisitorType> document,
            final PathIterationStateProvider arrayAllIterationState) {
        return StringLiteral.of(document.getSourcePath());
    }
}
