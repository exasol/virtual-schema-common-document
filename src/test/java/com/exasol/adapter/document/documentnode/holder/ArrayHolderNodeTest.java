package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class ArrayHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(ArrayHolderNode.class).verify();
    }
}
