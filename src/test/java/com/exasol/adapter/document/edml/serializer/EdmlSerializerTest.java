package com.exasol.adapter.document.edml.serializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.edml.*;

class EdmlSerializerTest {
    @Test
    void testSerialization() {
        final EdmlDefinition edmlDefinition = EdmlDefinition.builder().source("test").destinationTable("test")
                .schema("mySchema")
                .mapping(Fields.builder().mapField("test", ToVarcharMapping.builder().build()).build()).build();
        final String serialized = new EdmlSerializer().serialize(edmlDefinition);
        final String expected = "{\"source\":\"test\",\"destinationTable\":\"test\",\"description\":\"\",\"addSourceReferenceColumn\":false,\"mapping\":{\"fields\":{\"test\":{\"toVarcharMapping\":{\"key\":\"none\",\"varcharColumnSize\":254,\"nonStringBehaviour\":\"CONVERT_OR_ABORT\",\"overflowBehaviour\":\"TRUNCATE\"}}}},\"$schema\":\"mySchema\"}";
        assertThat(serialized, equalTo(expected));
    }

    @Test
    void testSerializeToTableMapping() {
        final EdmlDefinition edmlDefinition = EdmlDefinition.builder().source("test").destinationTable("test")
                .schema("mySchema").mapping(Fields.builder().mapField("test", //
                        ToTableMapping.builder().mapping(Fields.builder()//
                                .mapField("id", ToVarcharMapping.builder().build())//
                                .build()).build())
                        .build())
                .build();
        final String serialized = new EdmlSerializer().serialize(edmlDefinition);
        final String expected = "{\"source\":\"test\",\"destinationTable\":\"test\",\"description\":\"\",\"addSourceReferenceColumn\":false,\"mapping\":{\"fields\":{\"test\":{\"toTableMapping\":{\"mapping\":{\"fields\":{\"id\":{\"toVarcharMapping\":{\"key\":\"none\",\"varcharColumnSize\":254,\"nonStringBehaviour\":\"CONVERT_OR_ABORT\",\"overflowBehaviour\":\"TRUNCATE\"}}}},\"description\":\"\"}}}},\"$schema\":\"mySchema\"}";
        assertThat(serialized, equalTo(expected));
    }
}