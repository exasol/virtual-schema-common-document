package com.exasol.adapter.document.mapping.converter;

import static com.exasol.adapter.document.ListUtils.union;

import java.util.*;
import java.util.stream.Collectors;

import com.exasol.adapter.document.edml.KeyType;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.errorreporting.ExaError;

/**
 * Each nested table should contain the key of its parent table, so that users can join them together. This class puts
 * the key of each table to all their nested tables.
 */
class ForeignKeyAdder implements StagingTableMapping.Transformer {

    @Override
    public StagingTableMapping apply(final StagingTableMapping tableMapping) {
        return applyRecursive(tableMapping, Collections.emptyList());
    }

    private StagingTableMapping applyRecursive(final StagingTableMapping tableMapping,
            final List<ColumnWithKeyInfo> foreignKey) {
        final StagingTableMapping tableMappingWithForeignKey = tableMapping
                .withColumns(union(tableMapping.getColumns(), markAsNonKeyColumns(foreignKey)));
        final List<StagingTableMapping> nestedTables = tableMappingWithForeignKey.getNestedTables();
        if (nestedTables.isEmpty()) {
            return tableMappingWithForeignKey;
        } else {
            final List<ColumnWithKeyInfo> keyColumns = getKey(tableMapping, foreignKey);
            final List<StagingTableMapping> newNestedTables = new ArrayList<>(nestedTables.size());
            for (final StagingTableMapping nestedTable : nestedTables) {
                newNestedTables.add(applyRecursive(nestedTable, keyColumns));
            }
            return tableMappingWithForeignKey.withNestedTables(newNestedTables);
        }
    }

    private List<ColumnWithKeyInfo> markAsNonKeyColumns(final List<ColumnWithKeyInfo> prefixedKeyColumns) {
        return prefixedKeyColumns.stream().map(column -> column.withKey(KeyType.NONE)).collect(Collectors.toList());
    }

    private List<ColumnWithKeyInfo> prefixWithTableName(final List<ColumnWithKeyInfo> keyColumns,
            final String tableName) {
        final List<ColumnWithKeyInfo> result = new ArrayList<>();
        for (final ColumnWithKeyInfo columnWithKey : keyColumns) {
            final ColumnMapping keyColumn = columnWithKey.getColumn();
            result.add(columnWithKey
                    .withColumn(keyColumn.withNewExasolName(tableName + "_" + keyColumn.getExasolColumnName())));
        }
        return result;
    }

    private List<ColumnWithKeyInfo> getKey(final StagingTableMapping tableMapping,
            final List<ColumnWithKeyInfo> foreignKey) {
        final List<ColumnWithKeyInfo> globalKey = tableMapping.getKeyOfType(KeyType.GLOBAL);
        final List<ColumnWithKeyInfo> localKey = tableMapping.getKeyOfType(KeyType.LOCAL);
        if (!globalKey.isEmpty()) {
            return prefixWithTableName(globalKey, tableMapping.getExasolName());
        } else if (!localKey.isEmpty()) {
            return union(prefixWithTableName(localKey, tableMapping.getExasolName()), foreignKey);
        } else {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-88").message(
                    "The table {{table}} doesn't have key columns even so it has nested tables. The key columns should have been added by KeyAdder before.",
                    tableMapping.getExasolName()).ticketMitigation().toString());
        }
    }
}
