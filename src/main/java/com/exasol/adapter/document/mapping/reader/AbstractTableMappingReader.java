package com.exasol.adapter.document.mapping.reader;

import java.util.*;

import javax.json.JsonObject;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.*;
import com.exasol.errorreporting.ExaError;

/**
 * This class builds {@link TableMapping}s from Exasol document mapping language definitions. If the definition contains
 * nested lists that are mapped using a {@code ToTableMapping}, then the nested table is built using a recursive call to
 * {@link NestedTableMappingReader}.
 */
abstract class AbstractTableMappingReader {
    final List<TableMapping> tables = new ArrayList<>();

    protected void readMappingDefinition(final JsonObject definition) {
        final SchemaMappingDefinitionLanguageVisitor visitor = new SchemaMappingDefinitionLanguageVisitor();
        visitor.visitMapping(definition.getJsonObject(EdmlConstants.MAPPING_KEY), getPathToTable(), null,
                !isNestedTable());
        if (visitor.hasNestedTables()) {
            readTableWithNestedTable(visitor, definition);
        } else {
            addTable(visitor.getAllColumns(), definition);
        }
    }

    private void readTableWithNestedTable(final SchemaMappingDefinitionLanguageVisitor visitor,
            final JsonObject definition) {
        final GlobalKey globalKey = buildGlobalKey(visitor);
        final List<ColumnMapping> allColumns = new ArrayList<>(globalKey.getOwnKeyColumns());
        allColumns.addAll(globalKey.getForeignKeyColumns());
        for (final ColumnMapping nonKeyColumn : visitor.getNonKeyColumns()) {
            if (!allColumns.contains(nonKeyColumn)) {
                allColumns.add(nonKeyColumn);
            }
        }
        final TableMapping rootTable = addTable(allColumns, definition);
        for (final NestedTableReader nestedTableReader : visitor.getNestedTableReaderQueue()) {
            this.tables.addAll(nestedTableReader.readNestedTable(rootTable, globalKey));
        }
    }

    private GlobalKey buildGlobalKey(final SchemaMappingDefinitionLanguageVisitor visitor) {
        final List<ColumnMapping> userDefinedKeyColumns = visitor.getKeyColumns();
        if (userDefinedKeyColumns.isEmpty()) {
            return generateGlobalKey(visitor.getAllColumns());
        } else {
            if (visitor.getKeyType().equals(ColumnMappingDefinitionKeyTypeReader.KeyType.LOCAL)) {
                return new GlobalKey(getForeignKey(), userDefinedKeyColumns);
            } else {
                return new GlobalKey(Collections.emptyList(), userDefinedKeyColumns);
            }
        }
    }

    public List<TableMapping> getTables() {
        return this.tables;
    }

    private TableMapping addTable(final List<ColumnMapping> columns, final JsonObject definition) {
        final ArrayList<ColumnMapping> allColumns = new ArrayList<>(columns);
        if (definition.getBoolean(EdmlConstants.ADD_SOURCE_AS_COLUMN_KEY, false)) {
            allColumns.add(new SourceReferenceColumnMapping());
        }
        final TableMapping table = createTable(allColumns);
        this.tables.add(table);
        return table;
    }

    protected abstract TableMapping createTable(List<ColumnMapping> columns);

    /**
     * This method is called if no key columns were defined in the schema mapping definition but key columns are
     * required (for a nested table).
     *
     * @param availableColumns list of column mappings that could be used for building a key
     * @return list containing the generated key columns
     */
    protected abstract GlobalKey generateGlobalKey(List<ColumnMapping> availableColumns);

    protected abstract DocumentPathExpression.Builder getPathToTable();

    protected abstract boolean isNestedTable();

    protected abstract List<ColumnMapping> getForeignKey();

    @FunctionalInterface
    private interface NestedTableReader {
        List<TableMapping> readNestedTable(TableMapping parentTable, GlobalKey parentKeyColumns);
    }

    protected static class GlobalKey {
        private final List<ColumnMapping> foreignKeyColumns;
        private final List<ColumnMapping> ownKeyColumns;

        public GlobalKey(final List<ColumnMapping> foreignKeyColumns, final List<ColumnMapping> ownKeyColumns) {
            this.foreignKeyColumns = foreignKeyColumns;
            this.ownKeyColumns = ownKeyColumns;
        }

        public List<ColumnMapping> getForeignKeyColumns() {
            return this.foreignKeyColumns;
        }

        public List<ColumnMapping> getOwnKeyColumns() {
            return this.ownKeyColumns;
        }
    }

    private static class SchemaMappingDefinitionLanguageVisitor {
        private static final String FIELDS_KEY = "fields";
        private static final String TO_TABLE_MAPPING_KEY = "toTableMapping";

        /**
         * Building tables for nested lists is delayed using this queue as they need the completely built table that
         * maps the object they are nested in.
         */
        private final Queue<NestedTableReader> nestedTableReaderQueue;
        private final List<ColumnMapping> nonKeyColumns;
        private final List<ColumnMapping> keyColumns;
        private ColumnMappingDefinitionKeyTypeReader.KeyType keyType;

        public SchemaMappingDefinitionLanguageVisitor() {
            this.nestedTableReaderQueue = new LinkedList<>();
            this.nonKeyColumns = new ArrayList<>();
            this.keyColumns = new ArrayList<>();
            this.keyType = ColumnMappingDefinitionKeyTypeReader.KeyType.NO_KEY;
        }

        public final void visitMapping(final JsonObject definition, final DocumentPathExpression.Builder sourcePath,
                final String propertyName, final boolean isRootLevel) {
            final String mappingKey = getMappingType(definition, sourcePath);
            if (mappingKey.equals(TO_TABLE_MAPPING_KEY)) {
                queueAddingNestedTable(definition.getJsonObject(TO_TABLE_MAPPING_KEY), sourcePath, propertyName);
            } else if (mappingKey.equals(FIELDS_KEY)) {
                visitChildren(definition.getJsonObject(FIELDS_KEY), sourcePath);
            } else {
                final JsonObject columnMappingDefinition = definition.getJsonObject(mappingKey);
                addColumn(ColumnMappingReader.getInstance().readColumnMapping(mappingKey, columnMappingDefinition,
                        sourcePath, propertyName, isRootLevel), columnMappingDefinition, sourcePath);
            }
        }

        private void addColumn(final ColumnMapping column, final JsonObject definition,
                final DocumentPathExpression.Builder sourcePath) {
            final ColumnMappingDefinitionKeyTypeReader.KeyType columnsKeyType = new ColumnMappingDefinitionKeyTypeReader()
                    .readKeyType(definition);
            if (columnsKeyType.equals(ColumnMappingDefinitionKeyTypeReader.KeyType.NO_KEY)) {
                this.nonKeyColumns.add(column);
            } else {
                if (this.keyType != columnsKeyType
                        && this.keyType != ColumnMappingDefinitionKeyTypeReader.KeyType.NO_KEY) {
                    throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("E-VSD-8").message(
                            "{{VIOLATION_POINTER|uq}}: This table already has a key of a different type (global/local).")
                            .mitigation("Please either define all keys of the table local or global.")
                            .parameter("VIOLATION_POINTER", sourcePath.build().toString()).toString());
                }
                this.keyType = columnsKeyType;
                this.keyColumns.add(column);
            }
        }

        private void queueAddingNestedTable(final JsonObject definition,
                final DocumentPathExpression.Builder sourcePath, final String propertyName) {
            this.nestedTableReaderQueue.add((parentTable, parentKeyColumns) -> new NestedTableMappingReader(definition,
                    parentTable, propertyName, sourcePath, parentKeyColumns).getTables());
        }

        private String getMappingType(final JsonObject definition, final DocumentPathExpression.Builder sourcePath) {
            final Set<String> keys = definition.keySet();
            if (keys.isEmpty()) {
                return "";
            } else if (keys.size() == 1) {
                return keys.iterator().next();
            } else {
                throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("E-VSD-9")
                        .message("{{VIOLATION_POINTER|uq}}: More than one mapping for a single property.")
                        .parameter("VIOLATION_POINTER", sourcePath.build().toString())
                        .mitigation("Please define only one mapping for one property.").toString());
            }
        }

        private void visitChildren(final JsonObject definition, final DocumentPathExpression.Builder sourcePath) {
            for (final String dynamodbPropertyName : definition.keySet()) {
                final DocumentPathExpression.Builder newBuilder = new DocumentPathExpression.Builder(sourcePath)
                        .addObjectLookup(dynamodbPropertyName);
                visitMapping(definition.getJsonObject(dynamodbPropertyName), newBuilder, dynamodbPropertyName, false);
            }
        }

        public List<ColumnMapping> getAllColumns() {
            final List<ColumnMapping> union = new ArrayList<>();
            union.addAll(this.keyColumns);
            union.addAll(this.nonKeyColumns);
            return union;
        }

        public List<ColumnMapping> getKeyColumns() {
            return this.keyColumns;
        }

        public List<ColumnMapping> getNonKeyColumns() {
            return this.nonKeyColumns;
        }

        public ColumnMappingDefinitionKeyTypeReader.KeyType getKeyType() {
            return this.keyType;
        }

        public Queue<NestedTableReader> getNestedTableReaderQueue() {
            return this.nestedTableReaderQueue;
        }

        public boolean hasNestedTables() {
            return !this.nestedTableReaderQueue.isEmpty();
        }
    }
}
