package com.exasol.adapter.document;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * This class implements a flat-map for iterators. That means for each item in the passed iterator a function is called
 * that again builds an iterator. This class then emits all items of this iterator and continues with the next element
 * of the source iterator.
 * 
 * @param <T> result type
 * @param <S> input iterator type
 */
public class FlatMapIterator<T, S> implements Iterator<T> {
    private final Iterator<S> source;
    private final Function<S, Iterator<T>> mapFunction;
    private Iterator<T> currentIterator = null;
    private T next = null;
    private boolean hasNext = false;

    /**
     * Create a new instance of {@link FlatMapIterator}.
     * 
     * @param source      source iterator
     * @param mapFunction map function
     */
    public FlatMapIterator(final Iterator<S> source, final Function<S, Iterator<T>> mapFunction) {
        this.source = source;
        this.mapFunction = mapFunction;
        loadNext();
    }

    private void loadNext() {
        while (true) {
            if (this.currentIterator != null && this.currentIterator.hasNext()) {
                this.next = this.currentIterator.next();
                this.hasNext = true;
                return;
            } else {
                if (this.source.hasNext()) {
                    this.currentIterator = this.mapFunction.apply(this.source.next());
                } else {
                    this.hasNext = false;
                    return;
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
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
}
