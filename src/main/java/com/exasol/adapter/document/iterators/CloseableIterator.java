package com.exasol.adapter.document.iterators;

import java.util.Iterator;

public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
    @Override
    void close();
}
