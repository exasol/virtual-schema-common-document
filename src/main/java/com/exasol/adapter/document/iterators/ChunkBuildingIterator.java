package com.exasol.adapter.document.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class is a wrapper for {@link Iterator} that groups the data in chunks.
 * 
 * @param <T> type of the wrapped iterator
 */
public class ChunkBuildingIterator<T> implements CloseableIterator<List<T>> {
    private final int chunkSize;
    private final CloseableIterator<T> source;
    private boolean hasNext = false;
    private List<T> nextChunk;

    /**
     * Create a new instance of {@link ChunkBuildingIterator}.
     *
     * @param source    iterator to wrap
     * @param chunkSize size of the chunks
     */
    public ChunkBuildingIterator(final CloseableIterator<T> source, final int chunkSize) {
        this.source = source;
        this.chunkSize = chunkSize;
        buildChunk();
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public List<T> next() {
        if (!this.hasNext) {
            throw new NoSuchElementException();
        }
        final List<T> thisChunk = this.nextChunk;
        buildChunk();
        return thisChunk;
    }

    private void buildChunk() {
        this.nextChunk = new ArrayList<>(this.chunkSize);
        for (int counter = 0; counter < this.chunkSize && this.source.hasNext(); counter++) {
            this.nextChunk.add(this.source.next());
        }
        this.hasNext = !this.nextChunk.isEmpty();
    }

    @Override
    public void close() {
        this.source.close();
    }
}
