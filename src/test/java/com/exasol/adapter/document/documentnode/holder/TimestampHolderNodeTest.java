package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TimestampHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(TimestampHolderNode.class).verify();
    }
}
