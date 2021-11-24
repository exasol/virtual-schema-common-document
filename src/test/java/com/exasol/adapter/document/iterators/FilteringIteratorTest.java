package com.exasol.adapter.document.iterators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class FilteringIteratorTest {
    @Test
    void testFiltering() {
        final List<Integer> result = new ArrayList<>();
        new FilteringIterator<>(new CloseableIteratorWrapper<>(List.of(1, 2, 3, 4).iterator()), x -> x % 2 == 0)
                .forEachRemaining(result::add);
        assertThat(result, Matchers.contains(2, 4));
    }

    @Test
    void testClose() {
        final CloseableIterator<Integer> spy = spy(new CloseableIteratorWrapper<>(List.of(1, 2, 3, 4).iterator()));
        final FilteringIterator<Integer> filteringIterator = new FilteringIterator<>(spy, x -> x % 2 == 0);
        filteringIterator.close();
        verify(spy).close();
    }
}