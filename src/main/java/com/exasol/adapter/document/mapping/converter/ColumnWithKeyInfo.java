package com.exasol.adapter.document.mapping.converter;

import com.exasol.adapter.document.edml.KeyType;
import com.exasol.adapter.document.mapping.ColumnMapping;

import lombok.Data;
import lombok.With;

/**
 * This adds information to a column if it's part of a key.
 */
@Data
@With
class ColumnWithKeyInfo {
    private final ColumnMapping column;
    private final KeyType key;
}
