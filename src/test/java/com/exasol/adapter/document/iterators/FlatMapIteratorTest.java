package com.exasol.adapter.document.iterators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class FlatMapIteratorTest {
    @Test
    void testFlatMap() {
        final List<Integer> result = new ArrayList<>();
        final FlatMapIterator<Integer, Integer> iterator = new FlatMapIterator<>(
                new CloseableIteratorWrapper<>(List.of(1, 3).iterator()),
                x -> new CloseableIteratorWrapper<>(List.of(x, x + 1).iterator()));
        iterator.forEachRemaining(result::add);
        assertThat(result, Matchers.contains(1, 2, 3, 4));
    }

    @Test
    void testIteratorsAreClosedClosed() {
        final AtomicBoolean isSpy1Closed = new AtomicBoolean(false);
        final CloseableIterator<Integer> spy1 = spy(
                new CloseableIteratorWrapper<>(List.of(1, 2).iterator(), () -> isSpy1Closed.set(true)));
        final CloseableIterator<Integer> spy2 = spy(
                new CloseableIteratorWrapper<>(List.of(3, 4).iterator(), () -> isSpy1Closed.set(true)));
        final CloseableIterator<CloseableIterator<Integer>> spys = spy(
                new CloseableIteratorWrapper<>(List.of(spy1, spy2).iterator()));
        final CloseableIterator<Integer> iterator = new FlatMapIterator<>(spys, spy -> spy);
        iterator.next();
        iterator.next();
        verify(spy1).close();
        assertTrue(isSpy1Closed.get());
        iterator.close();
        verify(spy2).close();
        verify(spys).close();
    }

    @Test
    void testEmpty() {
        final FlatMapIterator<Integer, Integer> iterator = new FlatMapIterator<>(
                new CloseableIteratorWrapper<>(Collections.emptyIterator()),
                x -> new CloseableIteratorWrapper<>(List.of(x, x + 1).iterator()));
        assertFalse(iterator.hasNext());
    }
}