package com.exasol.adapter.document.edml;

/**
 * Interface for EDML mapping definitions.
 */
public interface MappingDefinition {
    /**
     * Accept a {@link MappingDefinitionVisitor}.
     * 
     * @param visitor visitor to accept
     */
    public void accept(MappingDefinitionVisitor visitor);
}
