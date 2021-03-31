package com.exasol.adapter.document.documentpath;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.ObjectHolderNode;
import com.exasol.adapter.document.documentnode.holder.StringHolderNode;

class LinearDocumentPathWalkerTest {

    private static final StringHolderNode NESTED_VALUE = new StringHolderNode("value");
    private static final ObjectHolderNode TEST_OBJECT_NODE = new ObjectHolderNode(Map.of("key", NESTED_VALUE));

    @Test
    void testWalk() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addObjectLookup("key").build();
        final Optional<DocumentNode> result = new LinearDocumentPathWalker<>(pathExpression)
                .walkThroughDocument(TEST_OBJECT_NODE);
        assertThat(result.orElse(null), equalTo(NESTED_VALUE));
    }

    @Test
    void testNonLinearPath() {
        final DocumentPathExpression pathExpression = DocumentPathExpression.builder().addArrayAll().build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new LinearDocumentPathWalker<>(pathExpression));
        assertThat(exception.getMessage(), startsWith("F-VSD-28: The given path is not a linear path."));
    }
}
