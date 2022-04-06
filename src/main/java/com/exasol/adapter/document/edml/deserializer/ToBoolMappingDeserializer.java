package com.exasol.adapter.document.edml.deserializer;

import static com.exasol.adapter.document.edml.deserializer.DeserializationHelper.readEnum;
import static com.exasol.adapter.document.edml.deserializer.MappingDeserializer.deserializeToColumnMapping;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;

import jakarta.json.JsonObject;

/**
 * Deserializer for {@link ToBoolMapping}.
 */
class ToBoolMappingDeserializer implements MappingDefinitionDeserializer {
    @Override
    public MappingDefinition deserialize(final JsonObject json) {
        final ToBoolMapping.ToBoolMappingBuilder<?, ?> builder = ToBoolMapping.builder();
        deserializeToColumnMapping(json, builder);
        readEnum(json, EdmlKeys.KEY_NOT_BOOLEAN_BEHAVIOR, ConvertableMappingErrorBehaviour.class)
                .ifPresent(builder::notBooleanBehavior);
        return builder.build();
    }

    @Override
    public Class<?> ofClass() {
        return ToBoolMapping.class;
    }
}
