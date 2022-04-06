package com.exasol.adapter.document.edml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.jqno.equalsverifier.EqualsVerifier;

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

    public static Stream<Arguments> getEdmlClasses() {
        return Stream.of(EdmlDefinition.class, Fields.class, ToBoolMapping.class, ToDecimalMapping.class,
                ToDecimalMapping.class, ToDoubleMapping.class, ToJsonMapping.class, ToTableMapping.class,
                ToTimestampMapping.class, ToVarcharMapping.class).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource({ "getEdmlClasses" })
    void testEquals(final Class<?> forClass) {
        EqualsVerifier.simple().forClass(forClass).verify();
    }
}
