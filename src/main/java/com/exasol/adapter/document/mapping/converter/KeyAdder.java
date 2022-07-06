package com.exasol.adapter.document.mapping.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.exasol.adapter.document.edml.ExasolDocumentMappingLanguageException;
import com.exasol.adapter.document.edml.KeyType;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.IterationIndexColumnMapping;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
import com.exasol.errorreporting.ExaError;

import lombok.AllArgsConstructor;

/**
 * This class tries to auto-generate these keys if they are not present.
 * <p>
 * Every table that has nested tables must have a local / global key.
 * </p>
 */
@AllArgsConstructor
class KeyAdder implements StagingTableMapping.Transformer {
    /**
     * Dependency injection of a {@link TableKeyFetcher}.
     */
    private final TableKeyFetcher tableKeyFetcher;

    @Override
    public StagingTableMapping apply(final StagingTableMapping tableMapping) {
        return applyRecursive(tableMapping);
    }

    private StagingTableMapping applyRecursive(final StagingTableMapping tableMapping) {
        final StagingTableMapping tableMappingWithKeyAdded = addKeyIfRequired(tableMapping);
        final List<StagingTableMapping> nestedTables = tableMappingWithKeyAdded.getNestedTables();
        final List<StagingTableMapping> newNestedTables = new ArrayList<>(nestedTables.size());
        for (final StagingTableMapping nestedTable : nestedTables) {
            newNestedTables.add(applyRecursive(nestedTable));
        }
        return tableMappingWithKeyAdded.withNestedTables(newNestedTables);
    }

    private StagingTableMapping addKeyIfRequired(final StagingTableMapping tableMapping) {
        if (tableMapping.hasNestedTables() && !hasKey(tableMapping)) {
            final List<ColumnWithKeyInfo> generatedKeyColumns = generateKeyOrThrow(tableMapping);
            final List<ColumnWithKeyInfo> newColumns = insertKeyColumnsInOriginalOrder(generatedKeyColumns,
                    tableMapping.getColumns());
            return tableMapping.withColumns(newColumns);
        } else {
            return tableMapping;
        }
    }

    private List<ColumnWithKeyInfo> insertKeyColumnsInOriginalOrder(final List<ColumnWithKeyInfo> generatedKeyColumns,
            final List<ColumnWithKeyInfo> oldColumns) {
        final List<ColumnWithKeyInfo> newColumns = new ArrayList<>();
        for (final ColumnWithKeyInfo oldColumn : oldColumns) {
            final Optional<ColumnWithKeyInfo> keyColumn = findColumnWithKeyThatMatchGiven(generatedKeyColumns,
                    oldColumn);
            if (keyColumn.isPresent()) {
                newColumns.add(keyColumn.get());
            } else {
                newColumns.add(oldColumn);
            }
        }
        // add remaining columns
        for (final ColumnWithKeyInfo keyColumn : generatedKeyColumns) {
            if (!newColumns.contains(keyColumn)) {
                newColumns.add(keyColumn);
            }
        }
        return newColumns;
    }

    private Optional<ColumnWithKeyInfo> findColumnWithKeyThatMatchGiven(final List<ColumnWithKeyInfo> columns,
            final ColumnWithKeyInfo otherColumn) {
        return columns.stream().filter(column -> column.getColumn().equals(otherColumn.getColumn())).findAny();
    }

    private boolean hasKey(final StagingTableMapping tableMapping) {
        return tableMapping.getColumns().stream()
                .anyMatch(column -> Set.of(KeyType.LOCAL, KeyType.GLOBAL).contains(column.getKey()));
    }

    private List<ColumnWithKeyInfo> generateKeyOrThrow(final StagingTableMapping tableMapping) {
        if (tableMapping.isNestedTable()) {
            final IterationIndexColumnMapping indexColumn = new IterationIndexColumnMapping("INDEX",
                    tableMapping.getPathInRemoteTable());
            return List.of(new ColumnWithKeyInfo(indexColumn, KeyType.LOCAL));
        } else {
            return generateKeyForRootTableOrThrow(tableMapping);
        }
    }

    private List<ColumnWithKeyInfo> generateKeyForRootTableOrThrow(final StagingTableMapping tableMapping) {
        final List<ColumnWithKeyInfo> columns = tableMapping.getColumns();
        try {
            return this.tableKeyFetcher.fetchKeyForTable(tableMapping.getRemoteName(), unpackColumns(columns)).stream()
                    .map(column -> new ColumnWithKeyInfo(column, KeyType.GLOBAL)).collect(Collectors.toList());
        } catch (final TableKeyFetcher.NoKeyFoundException exception) {
            throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("E-VSD-46")
                    .message("Could not infer keys for table {{TABLE}}.")
                    .parameter("TABLE", tableMapping.getExasolName())
                    .mitigation("Define a unique key by setting key='global' for one or more columns.").toString());
        }
    }

    private List<ColumnMapping> unpackColumns(final List<ColumnWithKeyInfo> columns) {
        return columns.stream().map(ColumnWithKeyInfo::getColumn).collect(Collectors.toList());
    }
}
