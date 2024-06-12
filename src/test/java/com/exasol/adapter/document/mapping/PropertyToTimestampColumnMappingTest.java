package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class PropertyToTimestampColumnMappingTest {
    private static final PropertyToTimestampColumnMapping TEST_OBJECT = configureExampleMapping(
            PropertyToTimestampColumnMapping.builder())//
            .notTimestampBehaviour(ConvertableMappingErrorBehaviour.NULL)//
            .build();

    @Test
    void testGetExasolDataType() {
        final PropertyToTimestampColumnMapping mapping = TEST_OBJECT.toBuilder().build();
        assertThat(mapping.getExasolDataType(), equalTo(DataType.createTimestamp(false, 6)));
    }

    @Test
    void testNewWithSameExasolName() {
        assertThat(TEST_OBJECT, equalTo(TEST_OBJECT.withNewExasolName(TEST_OBJECT.getExasolColumnName())));
    }

    @Test
    void testNewWithDifferentExasolName() {
        assertThat(TEST_OBJECT.withNewExasolName("other").getExasolColumnName(), equalTo("other"));
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(PropertyToTimestampColumnMapping.class).withRedefinedSuperclass().usingGetClass()
                .verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(PropertyToTimestampColumnMapping.class).verify();
    }
}
