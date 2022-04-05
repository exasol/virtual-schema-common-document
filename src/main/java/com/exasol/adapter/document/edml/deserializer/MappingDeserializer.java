package com.exasol.adapter.document.edml.deserializer;

import static com.exasol.adapter.document.edml.EdmlKeys.*;
import static com.exasol.adapter.document.edml.deserializer.DeserializationHelper.*;

import java.util.*;
import java.util.stream.Collectors;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.mapping.MappingErrorBehaviour;
import com.exasol.errorreporting.ExaError;

import jakarta.json.JsonObject;

class MappingDeserializer {
    static void deserializeToColumnMapping(final JsonObject json,
            final AbstractToColumnMapping.AbstractToColumnMappingBuilder<?, ?> builder) {
        Optional.ofNullable(json.getString(KEY_DESTINATION_NAME, null)).ifPresent(builder::destinationName);
        Optional.ofNullable(json.getString(KEY_DESCRIPTION, null)).ifPresent(builder::description);
        readEnum(json, KEY_KEY, KeyType.class).ifPresent(builder::key);
        readOptionalBoolean(json, KEY_REQUIRED).ifPresent(builder::required);
    }

    static void deserializeToNumberMapping(final JsonObject json,
            final AbstractToNumberMapping.AbstractToNumberMappingBuilder<?, ?> builder) {
        deserializeToColumnMapping(json, builder);
        readEnum(json, KEY_OVERFLOW_BEHAVIOUR, MappingErrorBehaviour.class).ifPresent(builder::overflowBehaviour);
        readEnum(json, KEY_NOT_NUMERIC_BEHAVIOUR, ConvertableMappingErrorBehaviour.class)
                .ifPresent(builder::notNumericBehaviour);
    }

    static void deserializeToVarcharColumnMapping(final JsonObject json,
            final AbstractToVarcharColumnMapping.AbstractToVarcharColumnMappingBuilder<?, ?> builder) {
        deserializeToColumnMapping(json, builder);
        readOptionalInt(json, KEY_VARCHAR_COLUMN_SIZE).ifPresent(builder::varcharColumnSize);
    }

    MappingDefinition deserializeMapping(final JsonObject mapping) {
        if (mapping.size() != 1) {
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-102")
                    .message("Invalid EDML definition. The mapping object must have exactly one property.").toString());
        }
        final String mappingKey = mapping.keySet().stream().findAny().orElseThrow();
        final List<MappingDefinitionDeserializer> deserializers = List.of(new FieldsDeserializer(),
                new ToDecimalMappingDeserializer(), new ToBoolMappingDeserializer(), new ToDateMappingDeserializer(),
                new ToDoubleMappingDeserializer(), new ToJsonMappingDeserializer(), new ToVarcharMappingDeserializer(),
                new ToTableMappingDeserializer(), new ToTimestampMappingDeserializer());
        final Map<String, MappingDefinitionDeserializer> deserializersByName = deserializers.stream()
                .collect(Collectors.toMap(deserializer -> deserializer.ofClass().getSimpleName().toLowerCase(),
                        deserializer -> deserializer));
        final MappingDefinitionDeserializer deserializer = deserializersByName.get(mappingKey.toLowerCase());
        if (deserializer == null) {
            final List<? extends Class<?>> possibleKeywords = deserializers.stream()
                    .map(MappingDefinitionDeserializer::ofClass).collect(Collectors.toList());
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-103").message(
                    "Invalid EDML definition. The mapping object only allows the following properties {{allowed}}.",
                    possibleKeywords).toString());
        }
        return deserializer.deserialize(mapping.getJsonObject(mappingKey));
    }
}
