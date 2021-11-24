package com.exasol.adapter.document.iterators;

import java.util.Iterator;

/**
 * Iterator for closable resources.
 * 
 * @param <T> type of the iterator
 */
public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
    @Override
    void close();
}
