package com.exasol.adapter.document.mapping.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.converter.StagingTableMapping.Transformer;

/**
 * This class is an abstract base for {@link Transformer}s that modify only the columns of an
 * {@link StagingTableMapping} and its nested tables.
 */
abstract class AbstractColumnModifier implements Transformer {
    @Override
    public final StagingTableMapping apply(final StagingTableMapping stagingTableMapping) {
        final List<ColumnWithKeyInfo> modifiedColumns = stagingTableMapping.getColumns().stream()
                .map(columnWithKeyInfo -> columnWithKeyInfo.withColumn(modify(columnWithKeyInfo.getColumn())))
                .collect(Collectors.toList());
        final StagingTableMapping tableWithModifiedColumns = stagingTableMapping.withColumns(modifiedColumns);
        return applyOnNestedTables(tableWithModifiedColumns);
    }

    private StagingTableMapping applyOnNestedTables(final StagingTableMapping tableWithModifiedColumns) {
        return tableWithModifiedColumns.withNestedTables(
                tableWithModifiedColumns.getNestedTables().stream().map(this::apply).collect(Collectors.toList()));
    }

    /**
     * Modify a column.
     * 
     * @param column column to modify
     * @return modified column
     */
    protected abstract ColumnMapping modify(ColumnMapping column);
}
