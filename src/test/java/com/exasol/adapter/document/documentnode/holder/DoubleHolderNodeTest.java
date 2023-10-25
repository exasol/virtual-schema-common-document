package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class DoubleHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(DoubleHolderNode.class).verify();
    }
}
