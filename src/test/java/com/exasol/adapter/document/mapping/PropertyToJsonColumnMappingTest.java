package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static com.exasol.adapter.metadata.DataType.ExaCharset.UTF8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class PropertyToJsonColumnMappingTest {

    private static final PropertyToJsonColumnMapping TEST_OBJECT = getDefaultTestObjectBuilder().build();

    private static PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder<?, ?> getDefaultTestObjectBuilder() {
        return configureExampleMapping(PropertyToJsonColumnMapping.builder())//
                .varcharColumnSize(10)//
                .overflowBehaviour(MappingErrorBehaviour.ABORT);
    }

    @Test
    void testGetExasolDataType() {
        assertThat(TEST_OBJECT.getExasolDataType(), equalTo(DataType.createVarChar(10, UTF8)));
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
        EqualsVerifier.forClass(PropertyToJsonColumnMapping.class).withRedefinedSuperclass().usingGetClass().verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(PropertyToJsonColumnMapping.class).verify();
    }
}
