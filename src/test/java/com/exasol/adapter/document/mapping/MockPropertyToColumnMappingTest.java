package com.exasol.adapter.document.mapping;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class MockPropertyToColumnMappingTest {
    @Test
    void testToString() {
        ToStringVerifier.forClass(MockPropertyToColumnMapping.class).verify();
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(MockPropertyToColumnMapping.class).verify();
    }
}
