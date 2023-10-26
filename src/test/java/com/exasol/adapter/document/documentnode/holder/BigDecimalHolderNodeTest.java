package com.exasol.adapter.document.documentnode.holder;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

class BigDecimalHolderNodeTest {
    @Test
    void equalsContract() {
        EqualsVerifier.forClass(BigDecimalHolderNode.class).suppress(Warning.BIGDECIMAL_EQUALITY).verify();
    }
}
