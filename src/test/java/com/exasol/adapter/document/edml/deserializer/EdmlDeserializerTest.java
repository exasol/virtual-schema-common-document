package com.exasol.adapter.document.edml.deserializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.edml.serializer.EdmlSerializer;
import com.exasol.adapter.document.mapping.*;

class EdmlDeserializerTest {
    @Test
    void testDeserializationOfMultipleFields() {
        final EdmlDefinition expected = EdmlDefinition.builder().source("test").destinationTable("test")
                .mapping(Fields.builder()//
                        .mapField("text", ToVarcharMapping.builder().build())
                        .mapField("number", ToDecimalMapping.builder().build())
                        .mapField("object", ToJsonMapping.builder().build()).build())
                .build();
        assertSerializeDeserializeLoop(expected);
    }

    private void assertSerializeDeserializeLoop(final EdmlDefinition expected) {
        final EdmlDefinition deserialized = new EdmlDeserializer()
                .deserialize(new EdmlSerializer().serialize(expected));
        assertThat(deserialized, equalTo(expected));
    }

    @Test
    void testDeserializeToVarcharMapping() {
        final ToVarcharMapping mapping = ToVarcharMapping.builder()
                .nonStringBehaviour(ConvertableMappingErrorBehaviour.NULL)
                .overflowBehaviour(TruncateableMappingErrorBehaviour.NULL).build();
        assertSerializeDeserializeLoop(getEdmlDefinitionForMapping(mapping));
    }

    @Test
    void testDeserializeToBoolMapping() {
        final var mapping = ToBoolMapping.builder().notBooleanBehavior(ConvertableMappingErrorBehaviour.NULL).build();
        assertSerializeDeserializeLoop(getEdmlDefinitionForMapping(mapping));
    }

    @Test
    void testDeserializeToDateMapping() {
        final var mapping = ToDateMapping.builder().notDateBehavior(ConvertableMappingErrorBehaviour.NULL).build();
        assertSerializeDeserializeLoop(getEdmlDefinitionForMapping(mapping));
    }

    @Test
    void testDeserializeToDecimalMapping() {
        final var mapping = ToDecimalMapping.builder().notNumericBehaviour(ConvertableMappingErrorBehaviour.NULL)
                .decimalPrecision(11).decimalScale(1).required(true).build();
        assertSerializeDeserializeLoop(getEdmlDefinitionForMapping(mapping));
    }

    @Test
    void testDeserializeToDoubleMapping() {
        final var mapping = ToDoubleMapping.builder().build();
        assertSerializeDeserializeLoop(getEdmlDefinitionForMapping(mapping));
    }

    @Test
    void testDeserializeToJsonMapping() {
        final var mapping = ToJsonMapping.builder().overflowBehaviour(MappingErrorBehaviour.NULL)
                .destinationName("test").key(KeyType.LOCAL).description("my description").varcharColumnSize(123)
                .build();
        assertSerializeDeserializeLoop(getEdmlDefinitionForMapping(mapping));
    }

    @Test
    void testDeserializeToTableMapping() {
        final var mapping = ToTableMapping.builder().description("my description").destinationTable("MY_NESTED")
                .mapping(Fields.builder().mapField("test", ToVarcharMapping.builder().build()).build()).build();
        assertSerializeDeserializeLoop(getEdmlDefinitionForMapping(mapping));
    }

    @Test
    void testDeserializeToTimestampMapping() {
        final var mapping = ToTimestampMapping.builder().notTimestampBehavior(ConvertableMappingErrorBehaviour.NULL)
                .useTimestampWithLocalTimezoneType(true).build();
        assertSerializeDeserializeLoop(getEdmlDefinitionForMapping(mapping));
    }

    private EdmlDefinition getEdmlDefinitionForMapping(final MappingDefinition mapping) {
        return EdmlDefinition.builder().source("test").destinationTable("test").mapping(Fields.builder()//
                .mapField("myProperty", mapping).build()).build();
    }
}