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

    /**
     * Get a builder for {@link Fields}.
     * 
     * @return {@link FieldsBuilder}
     */
    public static FieldsBuilder builder() {
        return new FieldsBuilder();
    }

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Builder for {@link Fields}.
     */
    public static class FieldsBuilder {
        private final LinkedHashMap<String, MappingDefinition> fields = new LinkedHashMap<>();

        /**
         * Add a field mapping.
         * 
         * @param filedName    name of the property in the document
         * @param fieldMapping {@link MappingDefinition}
         * @return self for fluent programming
         */
        public FieldsBuilder mapField(final String filedName, final MappingDefinition fieldMapping) {
            this.fields.put(filedName, fieldMapping);
            return this;
        }

        /**
         * Build the {@link Fields}.
         * 
         * @return built {@link Fields}
         */
        public Fields build() {
            return new Fields(this.fields);
        }
    }
}
