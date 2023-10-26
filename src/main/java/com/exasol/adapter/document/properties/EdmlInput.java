package com.exasol.adapter.document.properties;

import java.util.Objects;

/**
 * This class represents a EDML mapping definition string combined with a description of its source.
 */
public final class EdmlInput {
    private final String edmlString;
    private final String source;

    /**
     * Create a new instance of {@link EdmlInput}.
     * 
     * @param edmlString EDML mapping definition
     * @param source     description of the source, used for debugging
     */
    public EdmlInput(final String edmlString, final String source) {
        this.edmlString = edmlString;
        this.source = source;
    }

    /**
     * Get the EDML mapping definition.
     * 
     * @return EDML mapping definition
     */
    public String getEdmlString() {
        return edmlString;
    }

    /**
     * Get the description of the source, used for debugging.
     * 
     * @return description of the source, used for debugging
     */
    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "EdmlInput(edmlString=" + this.getEdmlString() + ", source=" + this.getSource() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(edmlString, source);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EdmlInput other = (EdmlInput) obj;
        return Objects.equals(edmlString, other.edmlString) && Objects.equals(source, other.source);
    }
}
