package com.exasol.adapter.document.edml.deserializer;

import com.exasol.adapter.document.edml.MappingDefinition;

import jakarta.json.JsonObject;

interface MappingDefinitionDeserializer {
    MappingDefinition deserialize(JsonObject json);

    Class<?> ofClass();
}
