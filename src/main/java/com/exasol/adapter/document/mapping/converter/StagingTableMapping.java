package com.exasol.adapter.document.mapping.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.edml.KeyType;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.TableMapping;

import lombok.Data;
import lombok.With;

/**
 * This data structure is an intermediate for the conversion from the {@link EdmlDefinition} to a {@link TableMapping}.
 * In contrast to the {@link TableMapping} it stores the tables still in a tree. That makes some transformation easier.
 * In addition, it also maintains the information which columns are key-columns.
 */
@Data
@With
class StagingTableMapping {
    private final String exasolName;
    private final String remoteName;
    private final List<ColumnWithKeyInfo> columns;
    private final DocumentPathExpression pathInRemoteTable;
    private final List<StagingTableMapping> nestedTables;

    /**
     * Get if this table mapping has nested table mappings.
     * 
     * @return {@code true} if it has
     */
    public boolean hasNestedTables() {
        return !this.nestedTables.isEmpty();
    }

    /**
     * Get if this is a mapping for a nested table.
     * 
     * @return {@code true} if it is
     */
    public boolean isNestedTable() {
        return this.pathInRemoteTable.size() > 0;
    }

    /**
     * Create a copy of this object, modified by a given transformer.
     * 
     * @param transformer transformer
     * @return modified copy
     */
    public StagingTableMapping transformedBy(final Transformer transformer) {
        return transformer.apply(this);
    }

    /**
     * Run a validation on this class.
     *
     * @param validator validator
     * @return self for fluent programming
     */
    public StagingTableMapping validateBy(final Validator validator) {
        validator.validate(this);
        return this;
    }

    /**
     * Get a copy of this object with one additional column.
     *
     * @param additionalColumn column to add
     * @return modified copy or unmodified this
     */
    public StagingTableMapping withAdditionalColumn(final ColumnWithKeyInfo additionalColumn) {
        final ArrayList<ColumnWithKeyInfo> newColumns = new ArrayList<>(this.columns);
        newColumns.add(additionalColumn);
        return this.withColumns(newColumns);
    }

    /**
     * Convert to {@link TableMapping}s.
     *
     * @return generated {@link TableMapping}s
     */
    public List<TableMapping> asToTableMappings() {
        final List<TableMapping> result = new ArrayList<>();
        result.add(asTableMapping());
        for (final StagingTableMapping nestedTable : this.nestedTables) {
            result.addAll(nestedTable.asToTableMappings());
        }
        return result;
    }

    private TableMapping asTableMapping() {
        return new TableMapping(this.exasolName, this.remoteName, getColumnsWithoutKeyInfo(), this.pathInRemoteTable);
    }

    private List<ColumnMapping> getColumnsWithoutKeyInfo() {
        return this.columns.stream().map(ColumnWithKeyInfo::getColumn).collect(Collectors.toList());
    }

    /**
     * Get the all columns of a given {@link KeyType}.
     *
     * @param type {@link KeyType} to search for
     * @return columns with given key type
     */
    public List<ColumnWithKeyInfo> getKeyOfType(final KeyType type) {
        return this.getColumns().stream().filter(column -> type.equals(column.getKey())).collect(Collectors.toList());
    }

    /**
     * Interface for classes that modify a {@link StagingTableMapping}.
     */
    public interface Transformer {
        /**
         * Apply transformation.
         *
         * @param stagingTableMapping {@link StagingTableMapping} to transform
         * @return transformed {@link StagingTableMapping}
         */
        public StagingTableMapping apply(StagingTableMapping stagingTableMapping);
    }

    /**
     * Interface for classes that validates a {@link StagingTableMapping}.
     */
    public interface Validator {
        /**
         * Run validation.
         *
         * @param stagingTableMapping {@link StagingTableMapping} to validate
         */
        public void validate(StagingTableMapping stagingTableMapping);
    }
}
