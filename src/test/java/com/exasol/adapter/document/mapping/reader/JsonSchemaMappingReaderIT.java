package com.exasol.adapter.document.mapping.reader;

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

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.mapping.*;
import com.exasol.adapter.document.mapping.TableKeyFetcher.NoKeyFoundException;
import com.exasol.adapter.document.properties.EdmlInput;

@Tag("integration")
@Tag("quick")
class JsonSchemaMappingReaderIT {

    @Test
    void testBasicMapping() {
        final SchemaMapping schemaMapping = read(JsonSample.builder() //
                .basic() //
                .withFields(JsonSample.ADDITIONAL_FIELDS) //
                .build());
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
        final SchemaMapping schemaMapping = read(JsonSample.builder().basic().build());
        final TableMapping table = schemaMapping.getTableMappings().get(0);
        assertThat(table.getColumns(), hasItem(new SourceReferenceColumnMapping()));
    }

    @Test
    void testWithoutSourcePathColumn() throws IOException {
        final SchemaMapping schemaMapping = read(JsonSample.builder() //
                .basic() //
                .addSourceReferenceColumn("  'addSourceReferenceColumn': false,") //
                .build());
        final TableMapping table = schemaMapping.getTableMappings().get(0);
        assertThat(table.getColumns(), not(hasItem(new SourceReferenceColumnMapping())));
    }

    @Test
    void testToJsonMapping() {
        final SchemaMapping schemaMapping = read(JsonSample.builder() //
                .basic() //
                .addSourceReferenceColumn("") //
                .withFields(JsonSample.TOPICS_JSON) //
                .build());
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
        final SchemaMapping schemaMapping = read(JsonSample.builder() //
                .basic() //
                .addSourceReferenceColumn("") //
                .withFields(JsonSample.TOPICS_TABLE) //
                .build());

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
        final String invalidString = JsonSample.builder() //
                .isbn(", 'key': 'global'") //
                .name(", 'key': 'local'") //
                .build();
        assertReaderThrowsExceptionMessage(invalidString, equalTo(
                "E-VSD-8: The table 'BOOKS' specified both local and global key columns: Local keys: ['NAME'], Global keys: ['ISBN']. That is not allowed. Use either a local or a global key."));
    }

    @Test
    void testLocalKeyAtRootLevelException() throws IOException {
        final String invalidString = JsonSample.builder() //
                .isbn(", 'key': 'local'") //
                .name("") //
                .withFields(JsonSample.TOPICS_TABLE) //
                .build();
        final Matcher<String> messageMatcher = equalTo("E-VSD-47: Invalid local key for column 'ISBN'."
                + " Local keys make no sense in root table mapping definitions. Please make this key global.");
        assertReaderThrowsExceptionMessage(invalidString, messageMatcher);
    }

    private void assertReaderThrowsExceptionMessage(final String invalidMapping, final Matcher<String> messageMatcher) {
        final ExasolDocumentMappingLanguageException exception = assertThrows(
                ExasolDocumentMappingLanguageException.class, () -> read(invalidMapping));
        assertAll(//
                () -> assertThat(exception.getMessage(),
                        startsWith("F-VSD-81: Semantic-validation error in schema mapping '")),
                () -> assertThat(exception.getCause().getMessage(), messageMatcher)//
        );
    }

    @Test
    void testNestedTableRootKeyGeneration() throws IOException {
        final SchemaMapping schemaMapping = read(JsonSample.builder() //
                .isbn("") //
                .name("") //
                .withFields(JsonSample.TOPICS_TABLE) //
                .build());
        final List<TableMapping> tables = schemaMapping.getTableMappings();
        final TableMapping nestedTable = tables.stream().filter(table -> !table.isRootTable()).findAny().orElseThrow();
        assertThat(getColumnNames(nestedTable.getColumns()), containsInAnyOrder("NAME", "BOOKS_ISBN"));
    }

    @Test
    void testNestedTableRootKeyGenerationException() throws IOException {
        final String mappingString = JsonSample.builder() //
                .name("") //
                .withFields(JsonSample.TOPICS_TABLE) //
                .build();
        assertReaderThrowsExceptionMessage(mappingString, equalTo(
                "E-VSD-46: Could not infer keys for table 'BOOKS'. Define a unique key by setting key='global' for one or more columns."));
    }

    @Test
    void testDoubleNestedToTableMapping() {
        final SchemaMapping schemaMapping = read(JsonSample.builder() //
                .addSourceReferenceColumn("") //
                .isbn("") //
                .name() //
                .withFields(JsonSample.DOUBLE_NESTED_TO_TABLE_MAPPING) //
                .build());
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

    private SchemaMapping read(final String mappingString) {
        return new JsonSchemaMappingReader(this::tableKeyFetcherMock)
                .readSchemaMapping(List.of(new EdmlInput(mappingString, "test")));
    }

    private List<ColumnMapping> tableKeyFetcherMock(final String tableName, final List<ColumnMapping> mappedColumns)
            throws NoKeyFoundException {
        final List<ColumnMapping> key = mappedColumns.stream().filter(this::isIsbnColumn).collect(Collectors.toList());
        if (key.isEmpty()) {
            throw new TableKeyFetcher.NoKeyFoundException();
        }
        return key;
    }

    private boolean isIsbnColumn(final ColumnMapping column) {
        if (column instanceof PropertyToColumnMapping) {
            final PropertyToColumnMapping propertyToColumnMapping = (PropertyToColumnMapping) column;
            return propertyToColumnMapping.getPathToSourceProperty().toString().endsWith("isbn");
        } else {
            return false;
        }
    }
}
