package com.exasol.adapter.document.edml.deserializer;

import java.io.IOException;

import com.exasol.adapter.document.edml.Fields;
import com.exasol.adapter.document.edml.MappingDefinition;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * EDML Deserializer for {@link Fields}.
 */
class FieldsDeserializer extends JsonDeserializer<Fields> {

    @Override
    public Fields deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
            throws IOException {
        final Fields.FieldsBuilder builder = Fields.builder();
        String nextFieldName;
        while ((nextFieldName = jsonParser.nextFieldName()) != null) {
            jsonParser.nextValue();
            builder.mapField(nextFieldName, jsonParser.readValueAs(MappingDefinition.class));
        }
        return builder.build();
    }
}
