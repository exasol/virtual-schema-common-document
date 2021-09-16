package com.exasol.adapter.document.edml.deserializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.edml.serializer.EdmlSerializer;

class EdmlDeserializerTest {
    @Test
    void testDeserialization() {
        final EdmlDefinition expected = EdmlDefinition.builder().source("test").destinationTable("test")
                .schema("https://schemas.exasol.com/edml-1.3.0.json").mapping(Fields.builder()//
                        .mapField("text", ToVarcharMapping.builder().build())
                        .mapField("number", ToDecimalMapping.builder().build())
                        .mapField("object", ToJsonMapping.builder().build()).build())
                .build();
        final EdmlDefinition deserialized = new EdmlDeserializer()
                .deserialize(new EdmlSerializer().serialize(expected));
        assertThat(deserialized, equalTo(expected));
    }
}