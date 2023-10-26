package com.exasol.adapter.document.properties;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class EdmlInputTest {
    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(EdmlInput.class).verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(EdmlInput.class).verify();
    }
}
