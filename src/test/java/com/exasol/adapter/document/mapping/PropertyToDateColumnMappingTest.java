package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

class PropertyToDateColumnMappingTest {
    private static final PropertyToDateColumnMapping TEST_OBJECT = configureExampleMapping(
            PropertyToDateColumnMapping.builder())//
                    .notDateBehaviour(ConvertableMappingErrorBehaviour.NULL)//
                    .build();

    @Test
    void testGetExasolDataType() {
        assertThat(TEST_OBJECT.getExasolDataType(), equalTo(DataType.createDate()));
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