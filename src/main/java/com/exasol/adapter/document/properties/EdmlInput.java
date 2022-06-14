package com.exasol.adapter.document.properties;

import lombok.Data;

/**
 * This class represents a EDML mapping definition string combined with a description of its source.
 */
@Data
public class EdmlInput {
    /** EDML mapping definition */
    private final String edmlString;
    /** Description of the source. Used for debugging. */
    private final String source;
}
