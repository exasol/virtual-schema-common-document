package com.exasol.adapter.document.edml.deserializer;

import static com.exasol.adapter.document.edml.EdmlKeys.KEY_OVERFLOW_BEHAVIOUR;
import static com.exasol.adapter.document.edml.deserializer.DeserializationHelper.readEnum;
import static com.exasol.adapter.document.edml.deserializer.MappingDeserializer.deserializeToVarcharColumnMapping;

import com.exasol.adapter.document.edml.MappingDefinition;
import com.exasol.adapter.document.edml.ToJsonMapping;
import com.exasol.adapter.document.mapping.MappingErrorBehaviour;

import jakarta.json.JsonObject;

/**
 * Deserializer for {@link ToJsonMapping}.
 */
class ToJsonMappingDeserializer implements MappingDefinitionDeserializer {
    @Override
    public MappingDefinition deserialize(final JsonObject json) {
        final ToJsonMapping.ToJsonMappingBuilder<?, ?> builder = ToJsonMapping.builder();
        deserializeToVarcharColumnMapping(json, builder);
        readEnum(json, KEY_OVERFLOW_BEHAVIOUR, MappingErrorBehaviour.class).ifPresent(builder::overflowBehaviour);
        return builder.build();
    }

    @Override
    public Class<?> ofClass() {
        return ToJsonMapping.class;
    }
}
