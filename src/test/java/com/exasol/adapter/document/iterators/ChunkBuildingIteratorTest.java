package com.exasol.adapter.document.iterators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class ChunkBuildingIteratorTest {

    @Test
    void testBuildChunk() {
        final ChunkBuildingIterator<Integer> iterator = new ChunkBuildingIterator<>(
                new CloseableIteratorWrapper<>(List.of(1, 2, 3).iterator()), 2);
        final List<List<Integer>> chunks = new ArrayList<>();
        iterator.forEachRemaining(chunks::add);
        assertThat(chunks, Matchers.contains(List.of(1, 2), List.of(3)));
    }

    @Test
    void testNoSuchElementException() {
        final ChunkBuildingIterator<Object> iterator = new ChunkBuildingIterator<>(
                new CloseableIteratorWrapper<>(Collections.emptyIterator()), 2);
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testClose() {
        final CloseableIterator<Integer> spy = spy(new CloseableIteratorWrapper<>(List.of(1, 2).iterator()));
        final ChunkBuildingIterator<Integer> chunkBuildingIterator = new ChunkBuildingIterator<>(spy, 2);
        chunkBuildingIterator.close();
        verify(spy).close();
    }
}