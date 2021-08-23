package com.exasol.adapter.document.edml;

import java.util.Map;

import lombok.*;

/**
 * Java representation of the {@code fields} object in the EDML.
 */
@Data
@Builder
public class Fields implements MappingDefinition {
    @Singular("mapField")
    @SuppressWarnings("java:S1700") // name is given by EDML
    private final Map<String, MappingDefinition> fields;
}
