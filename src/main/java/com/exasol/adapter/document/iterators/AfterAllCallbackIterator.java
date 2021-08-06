package com.exasol.adapter.document.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This {@link Iterator} invokes a method when {@link #hasNext()} returns false the first time or when {@link #next()}
 * is called but no more elements are available.
 * <p>
 * You can use this iterator for example to close a resource.
 * </p>
 * 
 * @param <T> type of the iterator
 */
public class AfterAllCallbackIterator<T> implements Iterator<T> {
    private final Iterator<T> source;
    private final Runnable callback;
    private boolean isCallbackTriggered = false;

    /**
     * Create a new instance of {@link AfterAllCallbackIterator}.
     * 
     * @param source   source iterator
     * @param callback callback function
     */
    public AfterAllCallbackIterator(final Iterator<T> source, final Runnable callback) {
        this.source = source;
        this.callback = callback;
    }

    @Override
    public boolean hasNext() {
        final boolean hasNext = this.source.hasNext();
        if (!hasNext && !this.isCallbackTriggered) {
            runCallback();
        }
        return hasNext;
    }

    private void runCallback() {
        this.callback.run();
        this.isCallbackTriggered = true;
    }

    @Override
    public T next() {
        try {
            return this.source.next();
        } catch (final NoSuchElementException exception) {
            if (!this.isCallbackTriggered) {
                runCallback();
            }
            throw exception;
        }
    }
}
