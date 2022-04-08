package com.exasol.adapter.document.mapping;

import java.util.Optional;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentpath.DocumentPathWalker;
import com.exasol.adapter.document.documentpath.PathIterationStateProvider;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.expression.ValueExpression;

/**
 * This class is the abstract basis for mapping a property of a document to an Exasol column. It provides functionality
 * for extracting the property described by the path in the {@link PropertyToColumnMapping}. The conversion of the value
 * is delegated to the implementation using the abstract method {@link #mapValue(DocumentNode)}.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public abstract class AbstractPropertyToColumnValueExtractor implements ColumnValueExtractor {
    private final PropertyToColumnMapping column;

    /**
     * Create an instance of {@link AbstractPropertyToColumnValueExtractor} for extracting a value specified parameter
     * column from a DynamoDB row.
     *
     * @param column {@link PropertyToColumnMapping} defining the mapping
     */
    AbstractPropertyToColumnValueExtractor(final PropertyToColumnMapping column) {
        this.column = column;
    }

    @Override
    public Object extractColumnValue(final FetchedDocument document,
            final PathIterationStateProvider arrayAllIterationState) {
        final DocumentPathWalker walker = new DocumentPathWalker(this.column.getPathToSourceProperty(),
                arrayAllIterationState);
        final Optional<DocumentNode> documentNode = walker.walkThroughDocument(document.getRootDocumentNode());
        if (documentNode.isEmpty()) {
            if (this.column.getLookupFailBehaviour() == MappingErrorBehaviour.NULL) {
                return null;
            } else {
                throw new SchemaMappingException(ExaError.messageBuilder("E-VSD-7")
                        .message("Could not find required property {{PROPERTY}} in the source document.")
                        .parameter("PROPERTY", this.column.getPathToSourceProperty(), "The missing property")
                        .toString());
            }
        } else {
            return mapValue(documentNode.get());
        }
    }

    /**
     * Converts a document property into an Exasol {@link ValueExpression}.
     *
     * @param documentValue the document value specified in the columns path expression to be converted
     * @return the conversion result
     * @throws ColumnValueExtractorException if the value can't be mapped
     */
    protected abstract Object mapValue(DocumentNode documentValue);
}
