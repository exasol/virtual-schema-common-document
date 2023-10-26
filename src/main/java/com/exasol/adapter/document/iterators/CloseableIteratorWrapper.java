package com.exasol.adapter.document.iterators;

import java.io.Closeable;
import java.util.Iterator;

import com.exasol.errorreporting.ExaError;

/**
 * Wrapper for iterators that adds a callback to close resources.
 * 
 * @param <T> type of the iterator
 */
public class CloseableIteratorWrapper<T> implements CloseableIterator<T> {
    private final Iterator<T> source;
    private final Closeable closeFunction;

    /**
     * Create a {@link CloseableIterator} for an iterator that does not need the close callback.
     * 
     * @param source iterator to wrap.
     */
    public CloseableIteratorWrapper(final Iterator<T> source) {
        this(source, () -> {
        });
    }

    /**
     * Create a {@link CloseableIterator}.
     * 
     * @param source        source iterator
     * @param closeFunction callback that is called when the iterator is closed
     */
    public CloseableIteratorWrapper(final Iterator<T> source, final Closeable closeFunction) {
        this.source = source;
        this.closeFunction = closeFunction;
    }

    @Override
    public boolean hasNext() {
        return this.source.hasNext();
    }

    @Override
    public T next() {
        return this.source.next();
    }

    @Override
    public void close() {
        try {
            this.closeFunction.close();
        } catch (final Exception exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-50").message("Failed to close resource.")
                    .ticketMitigation().toString(), exception);
        }
    }
}
