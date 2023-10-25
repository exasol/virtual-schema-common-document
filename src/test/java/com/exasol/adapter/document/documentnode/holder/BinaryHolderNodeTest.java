package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class BinaryHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(BinaryHolderNode.class).verify();
    }
}
