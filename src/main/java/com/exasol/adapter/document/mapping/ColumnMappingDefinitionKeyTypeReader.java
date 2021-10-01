package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.edml.KeyType;

import jakarta.json.JsonObject;

/**
 * This class reads the {@code key} property of an Exasol document mapping language column mapping definition.
 */
public class ColumnMappingDefinitionKeyTypeReader {
    private static final String KEY_KEY = "key";
    private static final String KEY_LOCAL = "local";
    private static final String KEY_GLOBAL = "global";

    /**
     * Reads the {@link KeyType} of a column mapping definition.
     *
     * @param definition the Exasol document mapping language definition of the column
     * @return read {@link KeyType}
     */
    public KeyType readKeyType(final JsonObject definition) {
        switch (definition.getString(KEY_KEY, "")) {
        case KEY_GLOBAL:
            return KeyType.GLOBAL;
        case KEY_LOCAL:
            return KeyType.LOCAL;
        default:
            return KeyType.NONE;
        }
    }

}
