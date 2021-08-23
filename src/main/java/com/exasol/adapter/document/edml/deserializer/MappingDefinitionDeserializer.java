package com.exasol.adapter.document.edml.deserializer;

import java.io.IOException;

import com.exasol.adapter.document.edml.*;
import com.exasol.errorreporting.ExaError;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * EDML Deserializer for {@link MappingDefinition}s.
 */
class MappingDefinitionDeserializer extends JsonDeserializer<MappingDefinition> {

    @Override
    public MappingDefinition deserialize(final JsonParser jsonParser,
            final DeserializationContext deserializationContext) throws IOException {
        final String nextFieldName = jsonParser.nextFieldName();
        jsonParser.nextValue();
        final MappingDefinition mappingDefinition = readMappingDefinition(jsonParser, nextFieldName);
        jsonParser.nextToken();// skip object end
        return mappingDefinition;
    }

    private MappingDefinition readMappingDefinition(final JsonParser jsonParser, final String nextFieldName)
            throws IOException {
        switch (nextFieldName) {
        case "fields":
            return jsonParser.readValueAs(Fields.class);
        case "toVarcharMapping":
            return jsonParser.readValueAs(ToVarcharMapping.class);
        case "toDecimalMapping":
            return jsonParser.readValueAs(ToDecimalMapping.class);
        case "toJsonMapping":
            return jsonParser.readValueAs(ToJsonMapping.class);
        case "toTableMapping":
            return jsonParser.readValueAs(ToTableMapping.class);
        default:
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-VSD-82").message("Unsupported mapping type {{type}}.", nextFieldName)
                            .mitigation("Please use one of the supported mapping types.").toString());
        }
    }
}
