package com.exasol.adapter.document.edml.serializer;

import static com.exasol.adapter.document.edml.EdmlKeys.*;

import java.io.*;
import java.util.Map;

import com.exasol.adapter.document.edml.*;
import com.exasol.errorreporting.ExaError;

import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;
import jakarta.json.spi.JsonProvider;
import lombok.Getter;

/**
 * JSON serializer for {@link EdmlDefinition}s.
 * <p>
 * Wondering why we don't use a tool like Jackson or JSON.bind here? Checkout the design.md (dsn~edml~serialization~1)
 * </p>
 */
//[impl->dsn~edml~serialization~1]
public class EdmlSerializer {
    private static final String SCHEMA = "https://schemas.exasol.com/edml-1.3.0.json";
    private static final JsonProvider JSON = JsonProvider.provider();

    private static JsonObjectBuilder serializeMapping(final MappingDefinition mappingDefinition) {
        final JsonObjectBuilder mappingBuilder = JSON.createObjectBuilder();
        if (mappingDefinition != null) {
            final String mappingName = toLowerCamelCase(mappingDefinition.getClass().getSimpleName());
            final MappingSerializingVisitor visitor = new MappingSerializingVisitor();
            mappingDefinition.accept(visitor);
            mappingBuilder.add(mappingName, visitor.getResult());
        }
        return mappingBuilder;
    }

    private static void addIfNotNull(final JsonObjectBuilder json, final String key, final String value) {
        if (value != null) {
            json.add(key, value);
        }
    }

    private static String toLowerCamelCase(final String mappingName) {
        return mappingName.substring(0, 1).toLowerCase() + mappingName.substring(1);
    }

    /**
     * Serialize the {@link EdmlDefinition}.
     * 
     * @param edmlDefinition {@link EdmlDefinition} to serialize
     * @return JSON representation
     */
    public String serialize(final EdmlDefinition edmlDefinition) {
        final JsonObjectBuilder edmlJson = JSON.createObjectBuilder();
        edmlJson.add(KEY_SCHEMA, SCHEMA);
        addIfNotNull(edmlJson, KEY_SOURCE, edmlDefinition.getSource());
        addIfNotNull(edmlJson, KEY_DESTINATION_TABLE, edmlDefinition.getDestinationTable());
        addIfNotNull(edmlJson, KEY_DESCRIPTION, edmlDefinition.getDescription());
        edmlJson.add(KEY_ADD_SOURCE_REFERENCE_COLUMN, edmlDefinition.isAddSourceReferenceColumn());
        edmlJson.add(KEY_MAPPING, serializeMapping(edmlDefinition.getMapping()));
        return toJson(edmlJson);
    }

    private String toJson(final JsonObjectBuilder edmlJson) {
        try (final StringWriter stringWriter = new StringWriter()) {
            try (final JsonWriter jsonWriter = JSON.createWriter(stringWriter)) {
                jsonWriter.write(edmlJson.build());
            }
            return stringWriter.toString();
        } catch (final IOException exception) {
            throw new UncheckedIOException(
                    ExaError.messageBuilder("F-VSD-100").message("Exception while serializing EDML.").toString(),
                    exception);
        }
    }

    private static class MappingSerializingVisitor implements MappingDefinitionVisitor {
        @Getter
        private final JsonObjectBuilder result = JSON.createObjectBuilder();

        @Override
        public void visit(final Fields fields) {
            for (final Map.Entry<String, MappingDefinition> entry : fields.getFields().entrySet()) {
                this.result.add(entry.getKey(), serializeMapping(entry.getValue()));
            }
        }

        private void visitToColumnMapping(final AbstractToColumnMapping toColumnMapping) {
            addIfNotNull(KEY_DESTINATION_NAME, toColumnMapping.getDestinationName());
            addIfNotNull(KEY_DESCRIPTION, toColumnMapping.getDescription());
            addLowercaseEnum(KEY_KEY, toColumnMapping.getKey());
            this.result.add(KEY_REQUIRED, toColumnMapping.isRequired());
        }

        private void visitToNumberMapping(final AbstractToNumberMapping toNumberMapping) {
            visitToColumnMapping(toNumberMapping);
            addUppercaseEnum(KEY_OVERFLOW_BEHAVIOUR, toNumberMapping.getOverflowBehaviour());
            addUppercaseEnum(KEY_NOT_NUMERIC_BEHAVIOUR, toNumberMapping.getNotNumericBehaviour());
        }

        private void visitAbstractToVarcharColumnMapping(final AbstractToVarcharColumnMapping mapping) {
            visitToColumnMapping(mapping);
            this.result.add(KEY_VARCHAR_COLUMN_SIZE, mapping.getVarcharColumnSize());
        }

        @Override
        public void visit(final ToDecimalMapping toDecimalMapping) {
            visitToNumberMapping(toDecimalMapping);
            this.result.add(KEY_DECIMAL_PRECISION, toDecimalMapping.getDecimalPrecision());
            this.result.add(KEY_DECIMAL_SCALE, toDecimalMapping.getDecimalScale());
        }

        @Override
        public void visit(final ToJsonMapping toJsonMapping) {
            visitAbstractToVarcharColumnMapping(toJsonMapping);
            addUppercaseEnum(KEY_OVERFLOW_BEHAVIOUR, toJsonMapping.getOverflowBehaviour());
        }

        @Override
        public void visit(final ToTableMapping toTableMapping) {
            addIfNotNull(KEY_DESTINATION_TABLE, toTableMapping.getDestinationTable());
            this.result.add(KEY_MAPPING, serializeMapping(toTableMapping.getMapping()));
            addIfNotNull(KEY_DESCRIPTION, toTableMapping.getDescription());
        }

        @Override
        public void visit(final ToVarcharMapping toVarcharMapping) {
            visitAbstractToVarcharColumnMapping(toVarcharMapping);
            addUppercaseEnum(KEY_NON_STRING_BEHAVIOUR, toVarcharMapping.getNonStringBehaviour());
            addUppercaseEnum(KEY_OVERFLOW_BEHAVIOUR, toVarcharMapping.getOverflowBehaviour());
        }

        @Override
        public void visit(final ToDoubleMapping toDoubleMapping) {
            visitToNumberMapping(toDoubleMapping);
        }

        @Override
        public void visit(final ToBoolMapping toBooleanMapping) {
            visitToColumnMapping(toBooleanMapping);
            addUppercaseEnum(KEY_NOT_BOOLEAN_BEHAVIOR, toBooleanMapping.getNotBooleanBehavior());
        }

        @Override
        public void visit(final ToDateMapping toDateMapping) {
            visitToColumnMapping(toDateMapping);
            addUppercaseEnum(KEY_NOT_DATE_BEHAVIOR, toDateMapping.getNotDateBehavior());
        }

        @Override
        public void visit(final ToTimestampMapping toTimestampMapping) {
            visitToColumnMapping(toTimestampMapping);
            addUppercaseEnum(KEY_NOT_TIMESTAMP_BEHAVIOR, toTimestampMapping.getNotTimestampBehavior());
            this.result.add(KEY_USE_TIMESTAMP_WITH_LOCAL_TIMEZONE_TYPE,
                    toTimestampMapping.isUseTimestampWithLocalTimezoneType());
        }

        private void addUppercaseEnum(final String key, final Enum<?> value) {
            if (value != null) {
                this.result.add(key, value.name());
            }
        }

        private void addLowercaseEnum(final String key, final Enum<?> value) {
            if (value != null) {
                this.result.add(key, value.name().toLowerCase());
            }
        }

        private void addIfNotNull(final String key, final String value) {
            EdmlSerializer.addIfNotNull(this.result, key, value);
        }
    }
}
