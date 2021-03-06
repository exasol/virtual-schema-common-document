package com.exasol.adapter.document.mapping.reader;

import static com.exasol.adapter.document.mapping.MappingTestFiles.generateInvalidFile;
import static com.exasol.adapter.document.mapping.MappingTestFiles.getMappingAsFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hamcrest.Matcher;
import org.json.JSONObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.document.mapping.*;

@Tag("integration")
@Tag("quick")
class JsonSchemaMappingReaderIT {
    @TempDir
    Path tempDir;

    private SchemaMapping getMappingDefinitionForFile(final File mappingFile) throws IOException, AdapterException {
        final SchemaMappingReader mappingFactory = new JsonSchemaMappingReader(mappingFile,
                (tableName, mappedColumns) -> {
                    final List<ColumnMapping> key = mappedColumns.stream()
                            .filter(column -> column.getExasolColumnName().equals("ISBN")).collect(Collectors.toList());
                    if (key.isEmpty()) {
                        throw new TableKeyFetcher.NoKeyFoundException();
                    }
                    return key;
                });
        return mappingFactory.getSchemaMapping();
    }

    /**
     * Tests schema load from basicMapping.json.
     */
    @Test
    void testBasicMapping() throws IOException, AdapterException {
        final SchemaMapping schemaMapping = getMappingDefinitionForFile(
                getMappingAsFile(MappingTestFiles.BASIC_MAPPING, this.tempDir));
        final List<TableMapping> tables = schemaMapping.getTableMappings();
        final TableMapping table = tables.get(0);
        final List<ColumnMapping> columns = table.getColumns();
        final Map<String, String> columnNames = getColumnNamesWithType(columns);
        final PropertyToVarcharColumnMapping isbnColumn = (PropertyToVarcharColumnMapping) getColumnByExasolName(table,
                "ISBN");
        final PropertyToVarcharColumnMapping nameColumn = (PropertyToVarcharColumnMapping) getColumnByExasolName(table,
                "NAME");
        assertAll(() -> assertThat(tables.size(), equalTo(1)), //
                () -> assertThat(table.getExasolName(), equalTo("BOOKS")),
                () -> assertThat(table.getRemoteName(), equalTo("MY_BOOKS")),
                () -> assertThat(columnNames,
                        equalTo(Map.of("ISBN", "VARCHAR(20) UTF8", "NAME", "VARCHAR(100) UTF8", "AUTHOR_NAME",
                                "VARCHAR(20) UTF8", "SOURCE_REFERENCE", "VARCHAR(2000) UTF8", "PUBLISHER",
                                "VARCHAR(100) UTF8", "PRICE", "DECIMAL(8, 2)"))),
                () -> assertThat(isbnColumn.getVarcharColumnSize(), equalTo(20)),
                () -> assertThat(isbnColumn.getOverflowBehaviour(), equalTo(TruncateableMappingErrorBehaviour.ABORT)),
                () -> assertThat(isbnColumn.getLookupFailBehaviour(), equalTo(MappingErrorBehaviour.ABORT)),
                () -> assertThat(nameColumn.getLookupFailBehaviour(), equalTo(MappingErrorBehaviour.NULL)),
                () -> assertThat(nameColumn.getOverflowBehaviour(),
                        equalTo(TruncateableMappingErrorBehaviour.TRUNCATE)));
    }

    @Test
    void testSourcePathColumn() throws IOException, AdapterException {
        final File mappingFile = generateInvalidFile(MappingTestFiles.BASIC_MAPPING,
                base -> base.put("addSourceReferenceColumn", true), this.tempDir);
        final SchemaMapping schemaMapping = getMappingDefinitionForFile(mappingFile);
        final TableMapping table = schemaMapping.getTableMappings().get(0);
        assertThat(table.getColumns(), hasItem(new SourceReferenceColumnMapping()));
    }

    @Test
    void testWithoutSourcePathColumn() throws IOException, AdapterException {
        final File mappingFile = generateInvalidFile(MappingTestFiles.BASIC_MAPPING,
                base -> base.put("addSourceReferenceColumn", false), this.tempDir);
        final SchemaMapping schemaMapping = getMappingDefinitionForFile(mappingFile);
        final TableMapping table = schemaMapping.getTableMappings().get(0);
        assertThat(table.getColumns(), not(hasItem(new SourceReferenceColumnMapping())));
    }

    @Test
    void testToJsonMapping() throws IOException, AdapterException {
        final SchemaMapping schemaMapping = getMappingDefinitionForFile(
                getMappingAsFile(MappingTestFiles.TO_JSON_MAPPING, this.tempDir));
        final List<TableMapping> tables = schemaMapping.getTableMappings();
        final TableMapping table = tables.get(0);
        final List<ColumnMapping> columns = table.getColumns();
        final List<String> columnNames = getColumnNames(columns);
        assertAll(() -> assertThat(tables.size(), equalTo(1)), //
                () -> assertThat(table.getExasolName(), equalTo("BOOKS")),
                () -> assertThat(columnNames, containsInAnyOrder("ISBN", "NAME", "TOPICS")));
    }

    private List<String> getColumnNames(final List<ColumnMapping> columns) {
        return columns.stream().map(ColumnMapping::getExasolColumnName).collect(Collectors.toList());
    }

    private Map<String, String> getColumnNamesWithType(final List<ColumnMapping> columns) {
        return columns.stream().collect(
                Collectors.toMap(ColumnMapping::getExasolColumnName, column -> column.getExasolDataType().toString()));
    }

    @Test
    void testToSingleColumnTableMapping() throws IOException, AdapterException {
        final SchemaMapping schemaMapping = getMappingDefinitionForFile(
                getMappingAsFile(MappingTestFiles.SINGLE_COLUMN_TO_TABLE_MAPPING, this.tempDir));
        final List<TableMapping> tables = schemaMapping.getTableMappings();
        final TableMapping nestedTable = tables.stream().filter(table -> !table.isRootTable()).findAny().orElseThrow();
        final PropertyToVarcharColumnMapping column = (PropertyToVarcharColumnMapping) getColumnByExasolName(
                nestedTable, "NAME");
        assertAll(//
                () -> assertThat(tables.size(), equalTo(2)),
                () -> assertThat(nestedTable.getExasolName(), equalTo("BOOKS_TOPICS")),
                () -> assertThat(getColumnNames(nestedTable.getColumns()), containsInAnyOrder("BOOKS_ISBN", "NAME")),
                () -> assertThat(column.getPathToSourceProperty().toString(), equalTo("/topics[*]"))//
        );
    }

    @Test
    void testToStringMappingAtRootLevelException() throws IOException, AdapterException {
        final File invalidFile = generateInvalidFile(MappingTestFiles.BASIC_MAPPING, base -> {
            final JSONObject newMappings = new JSONObject();
            newMappings.put("toVarcharMapping", new JSONObject());
            base.put("mapping", newMappings);
            return base;
        }, this.tempDir);
        assertReaderThrowsExceptionMessage(invalidFile, equalTo(
                "F-VSD-50: The mapping type 'toVarcharMapping' is not allowed at root level. You probably want to replace it with a 'fields' definition."));
    }

    @Test
    void testDifferentKeysException() throws IOException {
        final File invalidFile = generateInvalidFile(MappingTestFiles.BASIC_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("name")
                    .getJSONObject("toVarcharMapping").put("key", "local");
            return base;
        }, this.tempDir);
        assertReaderThrowsExceptionMessage(invalidFile, equalTo(
                "E-VSD-8: /name: This table already has a key of a different type (global/local). Please either define all keys of the table local or global."));
    }

    @Test
    void testLocalKeyAtRootLevelException() throws IOException {
        final File invalidFile = generateInvalidFile(MappingTestFiles.SINGLE_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("isbn")
                    .getJSONObject("toVarcharMapping").put("key", "local");
            return base;
        }, this.tempDir);

        final Matcher<String> messageMatcher = equalTo(
                "E-VSD-47: Local keys make no sense in root table mapping definitions. Please make this key global.");
        assertReaderThrowsExceptionMessage(invalidFile, messageMatcher);
    }

    private void assertReaderThrowsExceptionMessage(final File invalidFile, final Matcher<String> messageMatcher) {
        final ExasolDocumentMappingLanguageException exception = assertThrows(
                ExasolDocumentMappingLanguageException.class, () -> getMappingDefinitionForFile(invalidFile));
        assertAll(//
                () -> assertThat(exception.getMessage(),
                        startsWith("F-VSD-81: Semantic-validation error in schema mapping '")),
                () -> assertThat(exception.getCause().getMessage(), messageMatcher)//
        );
    }

    @Test
    void testNestedTableRootKeyGeneration() throws IOException, AdapterException {
        final File mappingFile = generateInvalidFile(MappingTestFiles.SINGLE_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("isbn")
                    .getJSONObject("toVarcharMapping").remove("key");
            return base;
        }, this.tempDir);
        final SchemaMapping schemaMapping = getMappingDefinitionForFile(mappingFile);
        final List<TableMapping> tables = schemaMapping.getTableMappings();
        final TableMapping nestedTable = tables.stream().filter(table -> !table.isRootTable()).findAny().orElseThrow();
        assertThat(getColumnNames(nestedTable.getColumns()), containsInAnyOrder("NAME", "BOOKS_ISBN"));
    }

    @Test
    void testNestedTableRootKeyGenerationException() throws IOException, AdapterException {
        final File mappingFile = generateInvalidFile(MappingTestFiles.SINGLE_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").remove("isbn");
            return base;
        }, this.tempDir);
        assertReaderThrowsExceptionMessage(mappingFile, equalTo(
                "E-VSD-46: Could not infer keys for table 'BOOKS'. Define a unique key by setting key='global' for one or more columns."));
    }

    @Test
    void testDoubleNestedToTableMapping() throws IOException, AdapterException {
        final SchemaMapping schemaMapping = getMappingDefinitionForFile(
                getMappingAsFile(MappingTestFiles.DOUBLE_NESTED_TO_TABLE_MAPPING, this.tempDir));
        final List<TableMapping> tables = schemaMapping.getTableMappings();
        final TableMapping doubleNestedTable = tables.stream()
                .filter(table -> table.getExasolName().equals("BOOKS_CHAPTERS_FIGURES")).findAny().orElseThrow();
        final TableMapping nestedTable = tables.stream().filter(table -> table.getExasolName().equals("BOOKS_CHAPTERS"))
                .findAny().orElseThrow();
        final TableMapping rootTable = tables.stream().filter(table -> table.getExasolName().equals("BOOKS")).findAny()
                .orElseThrow();
        final PropertyToColumnMapping foreignKey1 = (PropertyToColumnMapping) getColumnByExasolName(nestedTable,
                "BOOKS_ISBN");
        final IterationIndexColumnMapping indexColumn = (IterationIndexColumnMapping) getColumnByExasolName(nestedTable,
                "INDEX");
        final PropertyToVarcharColumnMapping figureNameColumn = (PropertyToVarcharColumnMapping) getColumnByExasolName(
                doubleNestedTable, "NAME");
        assertAll(//
                () -> assertThat(tables.size(), equalTo(3)),
                () -> assertThat(getColumnNames(rootTable.getColumns()), containsInAnyOrder("ISBN", "NAME")),
                () -> assertThat(getColumnNames(nestedTable.getColumns()),
                        containsInAnyOrder("BOOKS_ISBN", "INDEX", "NAME")),
                () -> assertThat(getColumnNames(doubleNestedTable.getColumns()),
                        containsInAnyOrder("BOOKS_ISBN", "BOOKS_CHAPTERS_INDEX", "NAME")),
                () -> assertThat(figureNameColumn.getPathToSourceProperty().toString(),
                        equalTo("/chapters[*]/figures[*]/name")),
                () -> assertThat(foreignKey1.getPathToSourceProperty().toString(), equalTo("/isbn")), //
                () -> assertThat(indexColumn.getTablesPath().toString(), equalTo("/chapters[*]")));
    }

    private ColumnMapping getColumnByExasolName(final TableMapping table, final String exasolName) {
        return table.getColumns().stream().filter(each -> each.getExasolColumnName().equals(exasolName)).findAny()
                .orElseThrow();
    }
}
