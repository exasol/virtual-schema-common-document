package com.exasol.adapter.document.edml.serializer;

import java.io.IOException;

import com.exasol.adapter.document.edml.KeyType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * EDML Serializer for {@link KeyType}.
 */
class KeyTypeSerializer extends JsonSerializer<KeyType> {
    @Override
    public void serialize(final KeyType keyType, final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(keyType.name().toLowerCase());
    }
}
