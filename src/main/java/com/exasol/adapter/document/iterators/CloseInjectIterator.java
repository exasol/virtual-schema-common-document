package com.exasol.adapter.document.iterators;

/**
 * {@link CloseableIterator} that injects an additional close function.
 * 
 * @param <T> iterator type
 */
public class CloseInjectIterator<T> implements CloseableIterator<T> {
    private final CloseableIterator<T> source;
    private final Runnable onClose;

    /**
     * Create a new instance of {@link CloseInjectIterator}.
     * 
     * @param source  source iterator
     * @param onClose callback that is called before the source iterator is closed
     */
    public CloseInjectIterator(final CloseableIterator<T> source, final Runnable onClose) {
        this.source = source;
        this.onClose = onClose;
    }

    @Override
    public void close() {
        this.onClose.run();
        this.source.close();
    }

    @Override
    public boolean hasNext() {
        return this.source.hasNext();
    }

    @Override
    public T next() {
        return this.source.next();
    }
}
