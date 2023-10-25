package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class BooleanHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(BooleanHolderNode.class).verify();
    }
}
