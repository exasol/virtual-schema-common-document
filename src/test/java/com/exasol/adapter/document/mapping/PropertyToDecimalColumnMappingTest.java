package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class PropertyToDecimalColumnMappingTest {
    private static final PropertyToDecimalColumnMapping TEST_OBJECT = configureExampleMapping(
            PropertyToDecimalColumnMapping.builder())//
            .decimalPrecision(12)//
            .decimalScale(1)//
            .overflowBehaviour(MappingErrorBehaviour.NULL)//
            .notNumericBehaviour(ConvertableMappingErrorBehaviour.NULL)//
            .build();

    @Test
    void testGetExasolDataType() {
        assertThat(TEST_OBJECT.getExasolDataType(), equalTo(DataType.createDecimal(12, 1)));
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
        EqualsVerifier.forClass(PropertyToDecimalColumnMapping.class).withRedefinedSuperclass().usingGetClass()
                .verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(PropertyToDecimalColumnMapping.class).verify();
    }
}
