package com.exasol.adapter.document.documentpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.MockArrayNode;
import com.exasol.adapter.document.documentnode.MockObjectNode;
import com.exasol.adapter.document.documentnode.MockValueNode;

class DocumentPathWalkerTest {

    private static final MockValueNode NESTED_VALUE1 = new MockValueNode("value");
    private static final MockValueNode NESTED_VALUE2 = new MockValueNode("value");
    private static final String ARRAY_KEY = "array_key";
    private static final String OBJECT_KEY = "array_key";
    private static final MockArrayNode TEST_ARRAY_NODE = new MockArrayNode(List.of(NESTED_VALUE1, NESTED_VALUE2));
    private static final MockObjectNode TEST_OBJECT_NODE = new MockObjectNode(
            Map.of("key", NESTED_VALUE1, OBJECT_KEY, TEST_ARRAY_NODE));
    private static final MockObjectNode TEST_NESTED_OBJECT_NODE = new MockObjectNode(
            Map.of(OBJECT_KEY, TEST_OBJECT_NODE));

    @Test
    void testWalkEmptyPath() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.empty();
        final Optional<DocumentNode<Object>> result = new DocumentPathWalker<Object>(pathExpression,
                new StaticDocumentPathIterator()).walkThroughDocument(TEST_OBJECT_NODE);
        assertThat(result.orElse(null), equalTo(TEST_OBJECT_NODE));
    }

    @Test
    void testWalkObjectPath() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addObjectLookup("key").build();
        final Optional<DocumentNode<Object>> result = new DocumentPathWalker<Object>(pathExpression,
                new StaticDocumentPathIterator()).walkThroughDocument(TEST_OBJECT_NODE);
        assertThat(result.orElse(null), equalTo(NESTED_VALUE1));
    }

    @Test
    void testNestedObject() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addObjectLookup(OBJECT_KEY)
                .addObjectLookup("key").build();
        final Optional<DocumentNode<Object>> result = new DocumentPathWalker<>(pathExpression,
                new StaticDocumentPathIterator()).walkThroughDocument(TEST_NESTED_OBJECT_NODE);
        assertThat(result.orElse(null), equalTo(NESTED_VALUE1));
    }

    @Test
    void testNotAnObject() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addObjectLookup("key")
                .addObjectLookup("key2").build();
        final DocumentPathWalker<Object> pathWalker = new DocumentPathWalker<>(pathExpression,
                new StaticDocumentPathIterator());
        final Optional<DocumentNode<Object>> result = pathWalker.walkThroughDocument(TEST_OBJECT_NODE);
        assertThat(result.orElse(null), is(nullValue()));
    }

    @Test
    void testUnknownProperty() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addObjectLookup("unknownKey")
                .build();
        final DocumentPathWalker<Object> pathWalker = new DocumentPathWalker<>(pathExpression,
                new StaticDocumentPathIterator());
        final Optional<DocumentNode<Object>> result = pathWalker.walkThroughDocument(TEST_OBJECT_NODE);
        assertThat(result.orElse(null), is(nullValue()));
    }

    @Test
    void testArrayLookup() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addArrayLookup(0).build();
        final Optional<DocumentNode<Object>> result = new DocumentPathWalker<Object>(pathExpression,
                new StaticDocumentPathIterator()).walkThroughDocument(TEST_ARRAY_NODE);
        assertThat(result.orElse(null), equalTo(NESTED_VALUE1));
    }

    @Test
    void testOutOfBoundsArrayLookup() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addArrayLookup(10).build();
        final DocumentPathWalker<Object> pathWalker = new DocumentPathWalker<>(pathExpression,
                new StaticDocumentPathIterator());
        final Optional<DocumentNode<Object>> result = pathWalker.walkThroughDocument(TEST_ARRAY_NODE);
        assertThat(result.orElse(null), is(nullValue()));
    }

    @Test
    void testArrayLookupOnNonArray() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addArrayLookup(10).build();
        final DocumentPathWalker<Object> pathWalker = new DocumentPathWalker<>(pathExpression,
                new StaticDocumentPathIterator());
        final Optional<DocumentNode<Object>> result = pathWalker.walkThroughDocument(TEST_OBJECT_NODE);
        assertThat(result.orElse(null), is(nullValue()));

    }

    @Test
    void testArrayAll() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addArrayAll().build();
        final Optional<DocumentNode<Object>> result1 = new DocumentPathWalker<Object>(pathExpression,
                new PathIterationStateProviderStub(0)).walkThroughDocument(TEST_ARRAY_NODE);
        final Optional<DocumentNode<Object>> result2 = new DocumentPathWalker<Object>(pathExpression,
                new PathIterationStateProviderStub(1)).walkThroughDocument(TEST_ARRAY_NODE);
        assertAll(() -> assertThat(result1.orElse(null), equalTo(NESTED_VALUE1)),
                () -> assertThat(result2.orElse(null), equalTo(NESTED_VALUE2)));
    }

    @Test
    void testNestedArrayAll() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addObjectLookup(OBJECT_KEY)
                .addArrayAll().build();
        final Optional<DocumentNode<Object>> result1 = new DocumentPathWalker<Object>(pathExpression,
                new PathIterationStateProviderStub(0)).walkThroughDocument(TEST_OBJECT_NODE);
        final Optional<DocumentNode<Object>> result2 = new DocumentPathWalker<Object>(pathExpression,
                new PathIterationStateProviderStub(1)).walkThroughDocument(TEST_OBJECT_NODE);
        assertAll(() -> assertThat(result1.orElse(null), equalTo(NESTED_VALUE1)),
                () -> assertThat(result2.orElse(null), equalTo(NESTED_VALUE2)));
    }

    private static class PathIterationStateProviderStub implements PathIterationStateProvider {
        final private int index;

        private PathIterationStateProviderStub(final int index) {
            this.index = index;
        }

        @Override
        public int getIndexFor(final DocumentPathExpression pathToArrayAll) {
            return this.index;
        }
    }
}