package com.exasol.adapter.document.edml.deserializer;

import static com.exasol.adapter.document.edml.EdmlKeys.*;

import java.util.Optional;

import com.exasol.adapter.document.edml.MappingDefinition;
import com.exasol.adapter.document.edml.ToTableMapping;

import jakarta.json.JsonObject;

/**
 * Deserializer for {@link ToTableMapping}.
 */
class ToTableMappingDeserializer implements MappingDefinitionDeserializer {
    @Override
    public MappingDefinition deserialize(final JsonObject json) {
        final ToTableMapping.ToTableMappingBuilder builder = ToTableMapping.builder();
        Optional.ofNullable(json.getString(KEY_DESTINATION_TABLE, null)).ifPresent(builder::destinationTable);
        builder.mapping(new MappingDeserializer().deserializeMapping(json.getJsonObject(KEY_MAPPING)));
        Optional.ofNullable(json.getString(KEY_DESCRIPTION, null)).ifPresent(builder::description);
        return builder.build();
    }

    @Override
    public Class<?> ofClass() {
        return ToTableMapping.class;
    }
}
