package com.exasol.adapter.document.edml.deserializer;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.edml.validator.EdmlSchemaValidator;
import com.exasol.errorreporting.ExaError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * This class deserializes an {@link EdmlDefinition} from JSON.
 */
public class EdmlDeserializer {
    /**
     * Deserialize an {@link EdmlDefinition} from JSON.
     * 
     * @param edmlDefinitionAsJson serialized JSON
     * @return deserialized {@link EdmlDefinition}
     */
    public EdmlDefinition deserialize(final String edmlDefinitionAsJson) {
        new EdmlSchemaValidator().validate(edmlDefinitionAsJson);
        final ObjectMapper objectMapper = getObjectMapper();
        try {
            return objectMapper.readValue(edmlDefinitionAsJson, EdmlDefinition.class);
        } catch (final JsonProcessingException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-VSD-85").message("Failed to deserialize EDML definition.").toString(),
                    exception);
        }
    }

    private ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(MappingDefinition.class, new MappingDefinitionDeserializer());
        module.addDeserializer(Fields.class, new FieldsDeserializer());
        module.addDeserializer(KeyType.class, new KeyTypeDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
