package com.exasol.adapter.document.edml.deserializer;

import static com.exasol.adapter.document.edml.EdmlKeys.KEY_NON_STRING_BEHAVIOUR;
import static com.exasol.adapter.document.edml.EdmlKeys.KEY_OVERFLOW_BEHAVIOUR;
import static com.exasol.adapter.document.edml.deserializer.DeserializationHelper.readEnum;
import static com.exasol.adapter.document.edml.deserializer.MappingDeserializer.deserializeToVarcharColumnMapping;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.mapping.TruncateableMappingErrorBehaviour;

import jakarta.json.JsonObject;

/**
 * Deserializer for {@link ToJsonMapping}.
 */
class ToVarcharMappingDeserializer implements MappingDefinitionDeserializer {
    @Override
    public MappingDefinition deserialize(final JsonObject json) {
        final ToVarcharMapping.ToVarcharMappingBuilder<?, ?> builder = ToVarcharMapping.builder();
        deserializeToVarcharColumnMapping(json, builder);
        readEnum(json, KEY_NON_STRING_BEHAVIOUR, ConvertableMappingErrorBehaviour.class)
                .ifPresent(builder::nonStringBehaviour);
        readEnum(json, KEY_OVERFLOW_BEHAVIOUR, TruncateableMappingErrorBehaviour.class)
                .ifPresent(builder::overflowBehaviour);
        return builder.build();
    }

    @Override
    public Class<?> ofClass() {
        return ToVarcharMapping.class;
    }
}
