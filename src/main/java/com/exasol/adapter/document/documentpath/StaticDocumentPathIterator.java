package com.exasol.adapter.document.documentpath;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.exasol.errorreporting.ExaError;

/**
 * This is an iterator that does exactly one iteration. It is used if no iteration needs to be done.
 */
public class StaticDocumentPathIterator implements Iterator<PathIterationStateProvider>, PathIterationStateProvider {
    private boolean called = false;

    @Override
    public boolean hasNext() {
        return !this.called;
    }

    @Override
    public PathIterationStateProvider next() {
        if (hasNext()) {
            this.called = true;
            return this;
        } else {
            throw new NoSuchElementException(ExaError.messageBuilder("F-VSD-66")
                    .message("The are no more combinations to iterate.").ticketMitigation().toString());
        }
    }

    @Override
    public int getIndexFor(final DocumentPathExpression pathToArrayAll) {
        throw new IllegalStateException(ExaError.messageBuilder("F-VSD-67")
                .message("The requested path is longer than the unwinded one.").ticketMitigation().toString());
    }
}
