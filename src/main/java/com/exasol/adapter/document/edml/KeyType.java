package com.exasol.adapter.document.edml;

/**
 * This enum defines column key types.
 */
public enum KeyType {
    /**
     * Key type that marks this column as non key column
     */
    NONE,

    /**
     * This key type marks a column a local key column. A local key is unique in the scope of a nested array but not
     * over the whole collection.
     */
    LOCAL,

    /**
     * This key type marks a column as global key column. A global key is unique over all rows. For tables that map
     * nested lists these rows typically result from different documents.
     */
    GLOBAL
}
