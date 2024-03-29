package com.exasol.adapter.document.iterators;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * This iterators wraps another iterator and filters its result based on a predicate.
 * 
 * @param <T> type of the source iterator
 */
public class FilteringIterator<T> implements CloseableIterator<T> {
    private final CloseableIterator<T> source;
    private final Predicate<T> predicate;
    private boolean hasNext;
    private T next;

    /**
     * Create a new instance of {@link FilteringIterator}.
     * 
     * @param source    iterator to filter
     * @param predicate predicate. Return {@code true} to include an item in the result
     */
    public FilteringIterator(final CloseableIterator<T> source, final Predicate<T> predicate) {
        this.source = source;
        this.predicate = predicate;
        loadNext();
    }

    private void loadNext() {
        while (this.source.hasNext()) {
            this.next = this.source.next();
            if (this.predicate.test(this.next)) {
                this.hasNext = true;
                return;
            }
        }
        this.hasNext = false;
    }

    @Override
    public T next() {
        if (!this.hasNext) {
            throw new NoSuchElementException();
        }
        final T nextCache = this.next;
        loadNext();
        return nextCache;
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public void close() {
        this.source.close();
    }
}
