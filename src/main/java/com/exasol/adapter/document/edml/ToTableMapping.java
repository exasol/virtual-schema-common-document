package com.exasol.adapter.document.edml;

import lombok.*;

/**
 * Java representation of the EDML {@code toTableMapping}.
 */
@Builder

@Data
@SuppressWarnings("java:S1170") // sonar can't deal with Lombok
public final class ToTableMapping implements MappingDefinition {
    private final String destinationTable;
    @NonNull
    private final MappingDefinition mapping;
    @Builder.Default
    private final String description = "";

    @Override
    public void accept(final MappingDefinitionVisitor visitor) {
        visitor.visit(this);
    }
}
