package com.exasol.adapter.document.edml.deserializer;

import static com.exasol.adapter.document.edml.deserializer.DeserializationHelper.readOptionalInt;
import static com.exasol.adapter.document.edml.deserializer.MappingDeserializer.deserializeToNumberMapping;

import com.exasol.adapter.document.edml.*;

import jakarta.json.JsonObject;

/**
 * Deserializer for {@link ToDecimalMapping}.
 */
class ToDecimalMappingDeserializer implements MappingDefinitionDeserializer {
    @Override
    public MappingDefinition deserialize(final JsonObject json) {
        final ToDecimalMapping.ToDecimalMappingBuilder<?, ?> builder = ToDecimalMapping.builder();
        deserializeToNumberMapping(json, builder);
        readOptionalInt(json, EdmlKeys.KEY_DECIMAL_PRECISION).ifPresent(builder::decimalPrecision);
        readOptionalInt(json, EdmlKeys.KEY_DECIMAL_SCALE).ifPresent(builder::decimalScale);
        return builder.build();
    }

    @Override
    public Class<?> ofClass() {
        return ToDecimalMapping.class;
    }
}
