package com.exasol.adapter.document.edml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class EdmlDefinitionTest {

    @Test
    void testBuilder() {
        final EdmlDefinition edmlDefinition = EdmlDefinition.builder().source("test").destinationTable("myTable")
                .mapping(Fields.builder().mapField("id", ToVarcharMapping.builder().build()).build()).build();
        final Fields fields = (Fields) edmlDefinition.getMapping();
        final ToVarcharMapping idField = (ToVarcharMapping) fields.getFields().get("id");
        assertAll(//
                () -> assertThat(edmlDefinition.getSource(), equalTo("test")),
                () -> assertThat(edmlDefinition.getDestinationTable(), equalTo("myTable")),
                () -> assertThat("254 is the default value", idField.getVarcharColumnSize(), equalTo(254))//
        );
    }
}
