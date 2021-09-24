package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;

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
     * @return java objects that the UDF emits
     * @throws ColumnValueExtractorException if specified property can't be mapped and {@link MappingErrorBehaviour} is
     *                                       set to {@code EXCEPTION }
     */
    Object extractColumnValue(final FetchedDocument document, final PathIterationStateProvider arrayAllIterationState);
}
