package com.exasol.adapter.document.mapping.converter;

import static java.util.Collections.emptyList;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class StagingTableMappingTest {
    @Test
    void testEqualsContract() {
        final List<Object> redValue = emptyList();
        final List<StagingTableMapping> blueValue = List.of(new StagingTableMapping("exasolName", "remoteName",
                "additionalConfiguration", emptyList(), null, emptyList()));
        EqualsVerifier.forClass(StagingTableMapping.class).withPrefabValues(List.class, redValue, blueValue).verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(StagingTableMapping.class).verify();
    }
}
