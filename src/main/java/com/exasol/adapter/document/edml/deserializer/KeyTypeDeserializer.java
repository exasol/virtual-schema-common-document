package com.exasol.adapter.document.edml.deserializer;

import java.io.IOException;

import com.exasol.adapter.document.edml.KeyType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * EDML Deserializer for {@link KeyType}.
 */
public class KeyTypeDeserializer extends JsonDeserializer<KeyType> {

    @Override
    public KeyType deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
            throws IOException {
        final JsonToken edmlName = jsonParser.nextValue();
        if (edmlName == null || edmlName.asString() == null || edmlName.asString().isEmpty()) {
            return KeyType.NONE;
        } else {
            return KeyType.valueOf(edmlName.asString());
        }
    }
}
