package com.exasol.adapter.document.iterators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class CloseInjectIteratorTest {
    @Test
    void testIteration() {
        final List<Integer> result = new ArrayList<>();
        try (CloseInjectIterator<Integer> iterator = new CloseInjectIterator<>(
                new CloseableIteratorWrapper<>(List.of(1, 2, 3).iterator()), () -> {
                })) {
            iterator.forEachRemaining(result::add);
        }
        assertThat(result, Matchers.contains(1, 2, 3));
    }

    @Test
    void testClose() {
        final CloseableIterator<?> source = mock(CloseableIterator.class);
        final Runnable closeFunction = mock(Runnable.class);
        new CloseInjectIterator<>(source, closeFunction).close();
        verify(closeFunction).run();
        verify(source).close();
    }
}