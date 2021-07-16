package com.exasol.adapter.document;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This {@link Iterator} invokes a method when {@link #hasNext()} returned false the first time or when {@link #next()}
 * was called but no more elements are available.
 * <p>
 * You can use this iterator for example to close a resource.
 * </p>
 * 
 * @param <T> type of the iterator
 */
public class OnNoMoreElementsIterator<T> implements Iterator<T> {
    private final Iterator<T> source;
    private final Runnable callback;
    private boolean wasTriggered = false;

    /**
     * Create a new instance of {@link OnNoMoreElementsIterator}.
     * 
     * @param source   source iterator
     * @param callback callback function
     */
    public OnNoMoreElementsIterator(final Iterator<T> source, final Runnable callback) {
        this.source = source;
        this.callback = callback;
    }

    @Override
    public boolean hasNext() {
        final boolean hasNext = this.source.hasNext();
        if (!hasNext && !this.wasTriggered) {
            runCallback();
        }
        return hasNext;
    }

    private void runCallback() {
        this.callback.run();
        this.wasTriggered = true;
    }

    @Override
    public T next() {
        try {
            return this.source.next();
        } catch (final NoSuchElementException exception) {
            if (!this.wasTriggered) {
                runCallback();
            }
            throw exception;
        }
    }
}
