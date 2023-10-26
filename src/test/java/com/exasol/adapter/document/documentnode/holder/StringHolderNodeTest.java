package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class StringHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(StringHolderNode.class).verify();
    }
}
