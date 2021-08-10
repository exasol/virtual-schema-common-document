package com.exasol.adapter.document.iterators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
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

    @Test
    void testNoSuchElementException() {
        final AfterAllCallbackIterator<Integer> iterator = getEmptyIterator();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    private AfterAllCallbackIterator<Integer> getEmptyIterator() {
        final AfterAllCallbackIterator<Integer> iterator = new AfterAllCallbackIterator<>(Collections.emptyIterator(),
                () -> {
                    // nothing to do
                });
        return iterator;
    }
}