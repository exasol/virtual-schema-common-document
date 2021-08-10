package com.exasol.adapter.document.iterators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class ChunkBuildingIteratorTest {

    @Test
    void testBuildChunk() {
        final ChunkBuildingIterator<Integer> iterator = new ChunkBuildingIterator<>(List.of(1, 2, 3).iterator(), 2);
        final List<List<Integer>> chunks = new ArrayList<>();
        iterator.forEachRemaining(chunks::add);
        assertThat(chunks, Matchers.contains(List.of(1, 2), List.of(3)));
    }

    @Test
    void testNoSuchElementException() {
        final ChunkBuildingIterator<Object> iterator = new ChunkBuildingIterator<>(Collections.emptyIterator(), 2);
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}