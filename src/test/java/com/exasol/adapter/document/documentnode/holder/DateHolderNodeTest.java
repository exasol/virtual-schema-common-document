package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class DateHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(DateHolderNode.class).verify();
    }
}
