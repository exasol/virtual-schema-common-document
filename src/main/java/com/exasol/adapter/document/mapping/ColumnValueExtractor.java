package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;
import com.exasol.sql.expression.ValueExpression;

/**
 * Interface for extracting a value specified in a {@link ColumnMapping} from a document.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public interface ColumnValueExtractor {

    /**
     * Extracts the columns values from the given document.
     *
     * @param document               to extract the value from
     * @param arrayAllIterationState array all iteration state used for extracting the correct values for nested lists
     * @return {@link ValueExpression}
     * @throws ColumnValueExtractorException if specified property can't be mapped and {@link MappingErrorBehaviour} is
     *                                       set to {@code EXCEPTION }
     */
    ValueExpression extractColumnValue(final FetchedDocument document,
            final PathIterationStateProvider arrayAllIterationState);
}
