package com.exasol.adapter.document.edml.deserializer;

import java.util.Map;

import com.exasol.adapter.document.edml.Fields;
import com.exasol.adapter.document.edml.MappingDefinition;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

/**
 * Deserializer for {@link Fields}.
 */
class FieldsDeserializer implements MappingDefinitionDeserializer {
    @Override
    public MappingDefinition deserialize(final JsonObject json) {
        final Fields.FieldsBuilder builder = Fields.builder();
        for (final Map.Entry<String, JsonValue> entry : json.entrySet()) {
            builder.mapField(entry.getKey(),
                    new MappingDeserializer().deserializeMapping(entry.getValue().asJsonObject()));
        }
        return builder.build();
    }

    @Override
    public Class<?> ofClass() {
        return Fields.class;
    }
}
