package com.exasol.adapter.document.documentpath;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.exasol.adapter.document.documentnode.DocumentNode;

/**
 * This factory creates a fitting {@link Iterator} for a given path and document.
 */
public class DocumentPathIteratorFactory implements Iterable<PathIterationStateProvider> {

    private final DocumentPathExpression path;
    private final DocumentNode document;

    /**
     * Create an instance of {@link DocumentPathIteratorFactory}.
     * 
     * @param path     path expression to iterate
     * @param document document
     */
    public DocumentPathIteratorFactory(final DocumentPathExpression path, final DocumentNode document) {
        this.path = path;
        this.document = document;
    }

    @Override
    public Iterator<PathIterationStateProvider> iterator() {
        if (this.path.indexOfFirstArrayAllSegment() == -1) {
            return new StaticDocumentPathIterator();
        } else {
            return new LoopDocumentPathIterator(this.path, this.document);
        }
    }

    /**
     * Get a stream of iteration states.
     * 
     * @return stream of {@link PathIterationStateProvider}
     */
    public Stream<PathIterationStateProvider> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
