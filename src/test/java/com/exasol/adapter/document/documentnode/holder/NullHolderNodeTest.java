package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class NullHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(NullHolderNode.class).verify();
    }
}
