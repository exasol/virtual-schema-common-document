package com.exasol.adapter.document.edml.serializer;

import java.io.IOException;

import com.exasol.adapter.document.edml.MappingDefinition;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * This custom serializer modifies the serialization of {@code to...Mappings}. That's required since they have a special syntax
 * in the EDML that they are identified by a key in an object.
 */
class MappingDefinitionSerializer extends JsonSerializer<MappingDefinition> {
    private final JsonSerializer<Object> defaultSerializer;

    /**
     * Create a new instance of {@link MappingDefinitionSerializer}.
     * 
     * @param defaultSerializer default serializer for this {@link MappingDefinition}.
     */
    MappingDefinitionSerializer(final JsonSerializer<Object> defaultSerializer) {
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(final MappingDefinition mapping, final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        final String mappingName = mapping.getClass().getSimpleName();
        final String nameStartingWithLowercase = mappingName.substring(0, 1).toLowerCase() + mappingName.substring(1);
        jsonGenerator.writeFieldName(nameStartingWithLowercase);
        this.defaultSerializer.serialize(mapping, jsonGenerator, serializerProvider);
        jsonGenerator.writeEndObject();
    }
}
