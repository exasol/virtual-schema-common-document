package com.exasol.adapter.document.iterators;

import java.util.Iterator;
import java.util.function.Function;

/**
 * This class applies a transformation function on an {@link Iterator}.
 * 
 * @param <T> input type
 * @param <R> output type
 */
public class TransformingIterator<T, R> implements CloseableIterator<R> {
    private final CloseableIterator<T> source;
    private final Function<T, R> mapFunction;

    /**
     * Create a new instance of {@link TransformingIterator}.
     * 
     * @param source      source iterator
     * @param mapFunction transformation function
     */
    public TransformingIterator(final CloseableIterator<T> source, final Function<T, R> mapFunction) {
        this.source = source;
        this.mapFunction = mapFunction;
    }

    @Override
    public boolean hasNext() {
        return this.source.hasNext();
    }

    @Override
    public R next() {
        return this.mapFunction.apply(this.source.next());
    }

    @Override
    public void close() {
        this.source.close();
    }
}
