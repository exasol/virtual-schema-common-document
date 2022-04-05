package com.exasol.adapter.document.edml.deserializer;

import static com.exasol.adapter.document.edml.deserializer.DeserializationHelper.readEnum;
import static com.exasol.adapter.document.edml.deserializer.MappingDeserializer.deserializeToColumnMapping;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;

import jakarta.json.JsonObject;

/**
 * Deserializer for {@link ToDateMapping}.
 */
class ToDateMappingDeserializer implements MappingDefinitionDeserializer {
    @Override
    public MappingDefinition deserialize(final JsonObject json) {
        final ToDateMapping.ToDateMappingBuilder<?, ?> builder = ToDateMapping.builder();
        deserializeToColumnMapping(json, builder);
        readEnum(json, EdmlKeys.KEY_NOT_DATE_BEHAVIOR, ConvertableMappingErrorBehaviour.class)
                .ifPresent(builder::notDateBehavior);
        return builder.build();
    }

    @Override
    public Class<?> ofClass() {
        return ToDateMapping.class;
    }
}
