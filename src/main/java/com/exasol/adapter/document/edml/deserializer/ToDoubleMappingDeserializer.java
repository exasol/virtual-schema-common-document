package com.exasol.adapter.document.edml.deserializer;

import static com.exasol.adapter.document.edml.deserializer.MappingDeserializer.deserializeToNumberMapping;

import com.exasol.adapter.document.edml.MappingDefinition;
import com.exasol.adapter.document.edml.ToDoubleMapping;

import jakarta.json.JsonObject;

/**
 * Deserializer for {@link ToDoubleMapping}.
 */
class ToDoubleMappingDeserializer implements MappingDefinitionDeserializer {
    @Override
    public MappingDefinition deserialize(final JsonObject json) {
        final ToDoubleMapping.ToDoubleMappingBuilder<?, ?> builder = ToDoubleMapping.builder();
        deserializeToNumberMapping(json, builder);
        return builder.build();
    }

    @Override
    public Class<?> ofClass() {
        return ToDoubleMapping.class;
    }
}
