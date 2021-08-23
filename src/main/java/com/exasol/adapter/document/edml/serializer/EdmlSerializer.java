package com.exasol.adapter.document.edml.serializer;

import java.util.Set;

import com.exasol.adapter.document.edml.*;
import com.exasol.errorreporting.ExaError;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * JSON serializer for {@link EdmlDefinition}s.
 */
public class EdmlSerializer {
    /**
     * Serialize the {@link EdmlDefinition}.
     * 
     * @param edmlDefinition {@link EdmlDefinition} to serialize
     * @return JSON representation
     */
    public String serialize(final EdmlDefinition edmlDefinition) {
        final ObjectMapper objectMapper = createObjectMapper();
        try {
            return objectMapper.writeValueAsString(edmlDefinition);
        } catch (final JsonProcessingException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-83")
                    .message("Failed to serialize EDML definition.").ticketMitigation().toString(), exception);
        }
    }

    private ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addSerializer(KeyType.class, new KeyTypeSerializer());
        module.setSerializerModifier(new Installer());
        objectMapper.registerModule(module);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /**
     * This helper class installs the custom serializers. Instead of simply adding them, it passes the default one to
     * the constructor so that the custom serializer can access the default one internally.
     */
    private static class Installer extends BeanSerializerModifier {

        private static final Set<Class<? extends MappingDefinition>> MAPPING_DEFINITIONS_WITH_SPECIAL_SERIALIZATION = Set
                .of(ToVarcharMapping.class, ToDecimalMapping.class, ToJsonMapping.class, ToTableMapping.class);

        @Override
        public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BeanDescription beanDesc,
                final JsonSerializer<?> serializer) {
            if (MAPPING_DEFINITIONS_WITH_SPECIAL_SERIALIZATION.contains(beanDesc.getBeanClass())) {
                return new MappingDefinitionSerializer((JsonSerializer<Object>) serializer);
            }
            return serializer;
        }
    }
}
