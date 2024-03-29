package com.exasol.adapter.document.documentpath;

import java.util.*;

import com.exasol.adapter.document.documentnode.DocumentArray;
import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.errorreporting.ExaError;

/**
 * This class iterates over {@link ArrayAllPathSegment}. It enumerates all combinations of arrays indexes for the arrays
 * in the given document that are matched by {@link ArrayAllPathSegment}s in the given path.
 *
 * For paths with multiple {@link ArrayAllPathSegment}s one object of this class only handles the first one and
 * delegates the remaining path to another instance ({@link #nestedIterator}).
 */
public class LoopDocumentPathIterator implements Iterator<PathIterationStateProvider> {
    private final DocumentPathExpression pathOfThisIterator;
    private final int arraySize;
    private final DocumentPathExpression pathOfNextIterator;
    private final DocumentArray arrayToIterate;
    private int currentIndex = -1;
    private Iterator<PathIterationStateProvider> nestedIterator;

    /**
     * Create an instance of {@link LoopDocumentPathIterator}.
     *
     * @param path     path definition used for extracting the {@link ArrayAllPathSegment}s to iterate
     * @param document document used for reading the array sizes
     */
    public LoopDocumentPathIterator(final DocumentPathExpression path, final DocumentNode document) {
        final int indexOfFirstArrayAllSegment = path.indexOfFirstArrayAllSegment();
        this.pathOfThisIterator = path.getSubPath(0, indexOfFirstArrayAllSegment + 1);
        this.pathOfNextIterator = path.getSubPath(indexOfFirstArrayAllSegment + 1, path.size());
        final DocumentPathExpression pathToThisArray = path.getSubPath(0, indexOfFirstArrayAllSegment);
        this.arrayToIterate = getArrayToIterate(document, pathToThisArray);
        this.arraySize = this.arrayToIterate == null ? 0 : this.arrayToIterate.size();
    }

    private DocumentArray getArrayToIterate(final DocumentNode document, final DocumentPathExpression pathToThisArray) {
        final Optional<DocumentNode> documentArray = new LinearDocumentPathWalker(pathToThisArray)
                .walkThroughDocument(document);
        if (documentArray.isEmpty()) {
            return null;
        } else {
            return (DocumentArray) documentArray.get();
        }
    }

    @Override
    public boolean hasNext() {
        int index = this.currentIndex;
        Iterator<PathIterationStateProvider> nextNestedIterator = this.nestedIterator;
        while (true) {
            if ((nextNestedIterator != null) && nextNestedIterator.hasNext()) {
                return true;
            } else if ((index + 1) < this.arraySize) {
                index++;
                nextNestedIterator = getNestedIteratorAtIndex(index);
            } else {
                return false;
            }
        }
    }

    /**
     * Moves iterator to the next combination.
     *
     * If the the {@link #nestedIterator} still has combinations these are taken first. Otherwise a new nested iterator
     * is build for the sub document of the next index of this iterator.
     *
     * @return {@code true} if could move to next; {@code false} if there was no remaining combination to iterate.
     */
    @Override
    public PathIterationStateProvider next() {
        while (true) {
            if ((this.nestedIterator != null) && this.nestedIterator.hasNext()) {
                return new IteratorState(this.pathOfThisIterator, this.currentIndex, this.nestedIterator.next());
            } else if ((this.currentIndex + 1) < this.arraySize) {// load next nested iterator
                this.currentIndex++;
                this.nestedIterator = getNestedIteratorAtIndex(this.currentIndex);
            } else {
                throw new NoSuchElementException(ExaError.messageBuilder("F-VSD-30")
                        .message("Internal error (The are no more combinations to iterate).").ticketMitigation()
                        .toString());
            }
        }
    }

    private Iterator<PathIterationStateProvider> getNestedIteratorAtIndex(final int index) {
        final DocumentNode subDocument = this.arrayToIterate.getValue(index);
        return new DocumentPathIteratorFactory(this.pathOfNextIterator, subDocument).iterator();
    }

    /**
     * This class represents the current iteration state of a {@link LoopDocumentPathIterator}.
     */
    private static class IteratorState implements PathIterationStateProvider {
        private final DocumentPathExpression pathOfThisIterator;
        private final int currentIndex;
        private final PathIterationStateProvider nextState;

        private IteratorState(final DocumentPathExpression pathOfThisIterator, final int currentIndex,
                final PathIterationStateProvider nextState) {
            this.pathOfThisIterator = pathOfThisIterator;
            this.currentIndex = currentIndex;
            this.nextState = nextState;
        }

        @Override
        public int getIndexFor(final DocumentPathExpression pathToRequestedArrayAll) {
            if (pathToRequestedArrayAll.equals(this.pathOfThisIterator)) {
                // This request is for our array
                return this.currentIndex;
            } else if ((this.nextState != null) && pathToRequestedArrayAll.startsWith(this.pathOfThisIterator)) {
                final DocumentPathExpression remainingPathToRequestedArrayAll = pathToRequestedArrayAll
                        .getSubPath(this.pathOfThisIterator.size(), pathToRequestedArrayAll.size());
                return this.nextState.getIndexFor(remainingPathToRequestedArrayAll);
            } else {
                throw new IllegalStateException(ExaError.messageBuilder("F-VSD-31")
                        .message("The requested path does not match the path that this iterator unwinds.")
                        .ticketMitigation().toString());
            }
        }
    }
}
