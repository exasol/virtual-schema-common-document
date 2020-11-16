package com.exasol.adapter.document.mapping.reader.validator.messageimprover;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.Schema;

import com.exasol.adapter.document.mapping.reader.validator.ExceptionMessageImprover;

public abstract class AbstractExceptionMessageImprover implements ExceptionMessageImprover {

    protected Set<String> possibleObjectProperties(final Schema schema) {
        try {
            final Set<String> possibleProperties = new HashSet<>();
            final ObjectSchema objectSchema = (ObjectSchema) schema;
            possibleProperties.addAll(objectSchema.getPropertySchemas().keySet());
            possibleProperties.addAll(possibleAdditionalObjectProperties(objectSchema));
            return possibleProperties;
        } catch (final ClassCastException | NullPointerException ignored) {
            return Collections.emptySet();
        }
    }

    private Set<String> possibleAdditionalObjectProperties(final ObjectSchema objectSchema) {
        try {
            final ObjectSchema additionalPropertiesSchema = getObjectSchema(
                    objectSchema.getSchemaOfAdditionalProperties());
            return additionalPropertiesSchema.getPropertySchemas().keySet();
        } catch (final ClassCastException | NullPointerException ignored) {
            return Collections.emptySet();
        }
    }

    private ObjectSchema getObjectSchema(final Schema schema) {
        if (schema instanceof ObjectSchema) {
            return (ObjectSchema) schema;
        }
        final ReferenceSchema referenceSchema = (ReferenceSchema) schema;
        return (ObjectSchema) referenceSchema.getReferredSchema();
    }
}
