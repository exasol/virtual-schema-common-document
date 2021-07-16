package com.exasol.adapter.document;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class OnNoMoreElementsIteratorTest {

    @Test
    void testCallback() {
        final AtomicBoolean wasCalled = new AtomicBoolean(false);
        final OnNoMoreElementsIterator<Integer> iterator = new OnNoMoreElementsIterator<>(List.of(1).iterator(),
                () -> wasCalled.set(true));
        iterator.hasNext();
        iterator.next();
        assertFalse(wasCalled.get());
        iterator.hasNext();
        assertTrue(wasCalled.get());
    }
}