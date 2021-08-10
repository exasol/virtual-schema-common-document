package com.exasol.adapter.document.iterators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class FlatMapIteratorTest {
    @Test
    void testFlatMap() {
        final List<Integer> result = new ArrayList<>();
        final FlatMapIterator<Integer, Integer> iterator = new FlatMapIterator<>(List.of(1, 3).iterator(),
                x -> List.of(x, x + 1).iterator());
        iterator.forEachRemaining(result::add);
        assertThat(result, Matchers.contains(1, 2, 3, 4));
    }

    @Test
    void testEmpty() {
        final FlatMapIterator<Integer, Integer> iterator = new FlatMapIterator<>(Collections.emptyIterator(),
                x -> List.of(x, x + 1).iterator());
        assertFalse(iterator.hasNext());
    }
}