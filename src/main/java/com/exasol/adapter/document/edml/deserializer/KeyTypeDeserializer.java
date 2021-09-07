package com.exasol.adapter.document.edml.deserializer;

import java.io.IOException;

import com.exasol.adapter.document.edml.KeyType;
import com.exasol.errorreporting.ExaError;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * EDML Deserializer for {@link KeyType}.
 */
public class KeyTypeDeserializer extends JsonDeserializer<KeyType> {

    @Override
    public KeyType deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
            throws IOException {
        final String edmlName = jsonParser.getCodec().readValue(jsonParser, String.class);
        if (edmlName == null || edmlName.isEmpty()) {
            return KeyType.NONE;
        } else {
            try {
                return KeyType.valueOf(edmlName.toUpperCase());
            } catch (final IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        ExaError.messageBuilder("E-VSD-86").message("Unknown key type {{type}}.", edmlName).toString());
            }
        }
    }
}
