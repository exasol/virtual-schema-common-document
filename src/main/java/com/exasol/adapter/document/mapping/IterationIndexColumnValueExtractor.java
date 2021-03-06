package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.IntegerLiteral;

/**
 * This class extracts the current array all iteration index as {@link ValueExpression}.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class IterationIndexColumnValueExtractor implements ColumnValueExtractor {
    private final IterationIndexColumnMapping column;

    /**
     * Create a new instance of {@link IterationIndexColumnValueExtractor}.
     * 
     * @param column column definition describing which array's index to read
     */
    IterationIndexColumnValueExtractor(final IterationIndexColumnMapping column) {
        this.column = column;
    }

    @Override
    public ValueExpression extractColumnValue(final FetchedDocument document,
            final PathIterationStateProvider arrayAllIterationState) {
        final int arrayIndex = arrayAllIterationState.getIndexFor(this.column.getTablesPath());
        return IntegerLiteral.of(arrayIndex);
    }
}
