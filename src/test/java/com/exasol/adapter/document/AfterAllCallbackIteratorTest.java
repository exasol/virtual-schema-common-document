package com.exasol.adapter.document;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

class AfterAllCallbackIteratorTest {

    @Test
    void testCallback() {
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        final AfterAllCallbackIterator<Integer> iterator = new AfterAllCallbackIterator<>(List.of(1).iterator(),
                () -> isCalled.set(true));
        iterator.hasNext();
        iterator.next();
        assertFalse(isCalled.get());
        iterator.hasNext();
        assertTrue(isCalled.get());
    }
}