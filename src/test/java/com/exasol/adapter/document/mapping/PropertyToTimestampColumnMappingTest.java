package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class PropertyToTimestampColumnMappingTest {
    private static final PropertyToTimestampColumnMapping TEST_OBJECT = configureExampleMapping(
            PropertyToTimestampColumnMapping.builder())//
            .notTimestampBehaviour(ConvertableMappingErrorBehaviour.NULL)//
            .build();

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testGetExasolDataType(final boolean withLocalTimezone) {
        final PropertyToTimestampColumnMapping mapping = TEST_OBJECT.toBuilder()
                .useTimestampWithLocalTimezoneType(withLocalTimezone).build();
        assertThat(mapping.getExasolDataType(), equalTo(DataType.createTimestamp(withLocalTimezone)));
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
