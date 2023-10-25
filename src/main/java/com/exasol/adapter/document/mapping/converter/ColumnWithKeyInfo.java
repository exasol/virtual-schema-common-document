package com.exasol.adapter.document.mapping.converter;

import com.exasol.adapter.document.edml.KeyType;
import com.exasol.adapter.document.mapping.ColumnMapping;

/**
 * This class stores a column together with information if it's part of a key.
 */
class ColumnWithKeyInfo {
    private final ColumnMapping column;
    private final KeyType key;

    ColumnWithKeyInfo(final ColumnMapping column, final KeyType key) {
        this.column = column;
        this.key = key;
    }

    ColumnMapping getColumn() {
        return column;
    }

    KeyType getKey() {
        return key;
    }

    ColumnWithKeyInfo withColumn(final ColumnMapping newColumn) {
        return new ColumnWithKeyInfo(newColumn, key);
    }

    ColumnWithKeyInfo withKey(final KeyType newKey) {
        return new ColumnWithKeyInfo(column, newKey);
    }
}
