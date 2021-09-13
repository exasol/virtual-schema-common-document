package com.exasol.adapter.document.mapping.reader;

import static com.exasol.adapter.document.mapping.MappingTestFiles.generateInvalid;
import static com.exasol.adapter.document.mapping.MappingTestFiles.getMappingAsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.edml.ExasolDocumentMappingLanguageException;
import com.exasol.adapter.document.mapping.*;
import com.exasol.adapter.document.properties.EdmlInput;

@Tag("integration")
@Tag("quick")
class JsonSchemaMappingReaderIT {

    private SchemaMapping runReader(final String mappingString) {
        final TableKeyFetcher tableKeyFetcherMock = (tableName, mappedColumns) -> {
            final List<ColumnMapping> key = mappedColumns.stream().filter(this::isIsbnColumn)
                    .collect(Collectors.toList());
            if (key.isEmpty()) {
                throw new TableKeyFetcher.NoKeyFoundException();
            }
            return key;
        };
        return new JsonSchemaMappingReader(tableKeyFetcherMock)
                .readSchemaMapping(List.of(new EdmlInput(mappingString, "test")));
    }

    private boolean isIsbnColumn(final ColumnMapping column) {
        if (column instanceof PropertyToColumnMapping) {
            final PropertyToColumnMapping propertyToColumnMapping = (PropertyToColumnMapping) column;
            return propertyToColumnMapping.getPathToSourceProperty().toString().endsWith("isbn");
        } else {
            return false;
        }
    }

    /**
     * Tests schema load from basicMapping.json.
     */
    @Test
    void testBasicMapping() {
        final SchemaMapping schemaMapping = runReader(getMappingAsString(MappingTestFiles.BASIC_MAPPING));
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
    void testSourcePathColumn() throws IOException {
        final String mappingString = generateInvalid(MappingTestFiles.BASIC_MAPPING,
                base -> base.put("addSourceReferenceColumn", true));
        final SchemaMapping schemaMapping = runReader(mappingString);
        final TableMapping table = schemaMapping.getTableMappings().get(0);
        assertThat(table.getColumns(), hasItem(new SourceReferenceColumnMapping()));
    }

    @Test
    void testWithoutSourcePathColumn() throws IOException {
        final String mappingString = generateInvalid(MappingTestFiles.BASIC_MAPPING,
                base -> base.put("addSourceReferenceColumn", false));
        final SchemaMapping schemaMapping = runReader(mappingString);
        final TableMapping table = schemaMapping.getTableMappings().get(0);
        assertThat(table.getColumns(), not(hasItem(new SourceReferenceColumnMapping())));
    }

    @Test
    void testToJsonMapping() {
        final SchemaMapping schemaMapping = runReader(getMappingAsString(MappingTestFiles.TO_JSON_MAPPING));
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
    void testToSingleColumnTableMapping() {
        final SchemaMapping schemaMapping = runReader(
                getMappingAsString(MappingTestFiles.SINGLE_COLUMN_TO_TABLE_MAPPING));
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
    void testDifferentKeysException() throws IOException {
        final String invalidString = generateInvalid(MappingTestFiles.BASIC_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("name")
                    .getJSONObject("toVarcharMapping").put("key", "local");
            return base;
        });
        assertReaderThrowsExceptionMessage(invalidString, equalTo(
                "E-VSD-8: The table 'BOOKS' specified both local and global key columns: Local keys: ['NAME'], Global keys: ['ISBN']. That is not allowed. Use either a local or a global key."));
    }

    @Test
    void testLocalKeyAtRootLevelException() throws IOException {
        final String invalidString = generateInvalid(MappingTestFiles.SINGLE_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("isbn")
                    .getJSONObject("toVarcharMapping").put("key", "local");
            return base;
        });

        final Matcher<String> messageMatcher = equalTo(
                "E-VSD-47: Invalid local key for column 'ISBN'. Local keys make no sense in root table mapping definitions. Please make this key global.");
        assertReaderThrowsExceptionMessage(invalidString, messageMatcher);
    }

    private void assertReaderThrowsExceptionMessage(final String invalidMapping, final Matcher<String> messageMatcher) {
        final ExasolDocumentMappingLanguageException exception = assertThrows(
                ExasolDocumentMappingLanguageException.class, () -> runReader(invalidMapping));
        assertAll(//
                () -> assertThat(exception.getMessage(),
                        startsWith("F-VSD-81: Semantic-validation error in schema mapping '")),
                () -> assertThat(exception.getCause().getMessage(), messageMatcher)//
        );
    }

    @Test
    void testNestedTableRootKeyGeneration() throws IOException {
        final String mappingString = generateInvalid(MappingTestFiles.SINGLE_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("isbn")
                    .getJSONObject("toVarcharMapping").remove("key");
            return base;
        });
        final SchemaMapping schemaMapping = runReader(mappingString);
        final List<TableMapping> tables = schemaMapping.getTableMappings();
        final TableMapping nestedTable = tables.stream().filter(table -> !table.isRootTable()).findAny().orElseThrow();
        assertThat(getColumnNames(nestedTable.getColumns()), containsInAnyOrder("NAME", "BOOKS_ISBN"));
    }

    @Test
    void testNestedTableRootKeyGenerationException() throws IOException {
        final String mappingString = generateInvalid(MappingTestFiles.SINGLE_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").remove("isbn");
            return base;
        });
        assertReaderThrowsExceptionMessage(mappingString, equalTo(
                "E-VSD-46: Could not infer keys for table 'BOOKS'. Define a unique key by setting key='global' for one or more columns."));
    }

    @Test
    void testDoubleNestedToTableMapping() {
        final SchemaMapping schemaMapping = runReader(
                getMappingAsString(MappingTestFiles.DOUBLE_NESTED_TO_TABLE_MAPPING));
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
