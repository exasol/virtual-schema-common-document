package com.exasol.adapter.document.iterators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class TransformingIteratorTest {

    @Test
    void testTransformation() {
        final List<Integer> result = new ArrayList<>();
        final TransformingIterator<Integer, Integer> iterator = new TransformingIterator<>(
                new CloseableIteratorWrapper<>(List.of(1, 2).iterator()), x -> x * 2);
        iterator.forEachRemaining(result::add);
        iterator.close();
        assertThat(result, Matchers.contains(2, 4));
    }

    @Test
    void testClose() {
        final CloseableIterator<Integer> spy = spy(new CloseableIteratorWrapper<>(List.of(1, 2).iterator()));
        final TransformingIterator<Integer, Integer> transformingIterator = new TransformingIterator<>(spy, x -> x + 1);
        transformingIterator.close();
        verify(spy).close();
    }
}