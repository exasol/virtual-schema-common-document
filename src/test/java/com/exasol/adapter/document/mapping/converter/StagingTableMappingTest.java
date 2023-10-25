package com.exasol.adapter.document.mapping.converter;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class StagingTableMappingTest {
    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(StagingTableMapping.class).verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(StagingTableMapping.class).verify();
    }
}
