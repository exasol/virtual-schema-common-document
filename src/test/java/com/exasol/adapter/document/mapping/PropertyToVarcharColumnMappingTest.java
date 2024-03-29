package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour.NULL;
import static com.exasol.adapter.document.edml.TruncateableMappingErrorBehaviour.TRUNCATE;
import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static com.exasol.adapter.metadata.DataType.ExaCharset.UTF8;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import com.exasol.adapter.metadata.DataType;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class PropertyToVarcharColumnMappingTest {
    private static final int STRING_LENGTH = 10;
    private static final PropertyToVarcharColumnMapping TEST_OBJECT = configureExampleMapping(
            PropertyToVarcharColumnMapping.builder())//
            .varcharColumnSize(STRING_LENGTH)//
            .overflowBehaviour(TRUNCATE).nonStringBehaviour(NULL).build();

    @Test
    void testGetExasolDataType() {
        assertThat(TEST_OBJECT.getExasolDataType(), CoreMatchers.equalTo(DataType.createVarChar(10, UTF8)));
    }

    @Test
    void testNewWithSameExasolName() {
        assertThat(TEST_OBJECT, CoreMatchers.equalTo(TEST_OBJECT.withNewExasolName(TEST_OBJECT.getExasolColumnName())));
    }

    @Test
    void testNewWithDifferentExasolName() {
        assertThat(TEST_OBJECT.withNewExasolName("other").getExasolColumnName(), CoreMatchers.equalTo("other"));
    }

    @Test
    void testEqualsContract() {
        EqualsVerifier.forClass(PropertyToVarcharColumnMapping.class).withRedefinedSuperclass().usingGetClass()
                .verify();
    }

    @Test
    void testToString() {
        ToStringVerifier.forClass(PropertyToVarcharColumnMapping.class).verify();
    }
}
