package com.exasol.adapter.document.iterators;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class TransformingIteratorTest {

    @Test
    void testTransformation() {
        final List<Integer> result = new ArrayList<>();
        new TransformingIterator<>(List.of(1, 2).iterator(), x -> x * 2).forEachRemaining(result::add);
        assertThat(result, Matchers.contains(2, 4));
    }
}