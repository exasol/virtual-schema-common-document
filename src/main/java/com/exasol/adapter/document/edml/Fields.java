package com.exasol.adapter.document.edml;

import java.util.LinkedHashMap;

import lombok.Data;
import lombok.Singular;

/**
 * Java representation of the {@code fields} object in the EDML.
 */
@Data
public class Fields implements MappingDefinition {
    @Singular("mapField")
    @SuppressWarnings("java:S1700") // name is given by EDML
    private final LinkedHashMap<String, MappingDefinition> fields;

    public static FieldsBuilder builder() {
        return new FieldsBuilder();
    }

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }

    public static class FieldsBuilder {
        private final LinkedHashMap<String, MappingDefinition> fields = new LinkedHashMap<>();

        public FieldsBuilder mapField(final String filedName, final MappingDefinition fieldMapping) {
            this.fields.put(filedName, fieldMapping);
            return this;
        }

        public Fields build() {
            return new Fields(this.fields);
        }
    }
}
