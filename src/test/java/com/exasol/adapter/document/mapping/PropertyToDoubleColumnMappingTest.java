package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.metadata.DataType;

class PropertyToDoubleColumnMappingTest {
    private static final PropertyToDoubleColumnMapping TEST_OBJECT = configureExampleMapping(
            PropertyToDoubleColumnMapping.builder())//
                    .overflowBehaviour(MappingErrorBehaviour.NULL)//
                    .notNumericBehaviour(ConvertableMappingErrorBehaviour.NULL)//
                    .build();

    @Test
    void testGetExasolDataType() {
        assertThat(TEST_OBJECT.getExasolDataType(), equalTo(DataType.createDouble()));
    }

    @Test
    void testNewWithSameExasolName() {
        assertThat(TEST_OBJECT, equalTo(TEST_OBJECT.withNewExasolName(TEST_OBJECT.getExasolColumnName())));
    }

    @Test
    void testNewWithDifferentExasolName() {
        assertThat(TEST_OBJECT.withNewExasolName("other").getExasolColumnName(), equalTo("other"));
    }
}