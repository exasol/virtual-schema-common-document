package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class ObjectHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(ObjectHolderNode.class).verify();
    }
}
