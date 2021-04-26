package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.getColumnMappingExample;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.sql.expression.IntegerLiteral;
import com.exasol.sql.expression.ValueExpression;

class SchemaMapperTest {

    @Test
    void testMapRow() {
        final PropertyToColumnMapping columnMapping = getColumnMappingExample().varcharColumnSize(254).build();
        final SchemaMappingRequest request = new SchemaMappingRequest(DocumentPathExpression.empty(),
                List.of(columnMapping));
        final SchemaMapper schemaMapper = new SchemaMapper(request);

        final List<List<ValueExpression>> result = new ArrayList<>();
        schemaMapper.mapRow(
                new FetchedDocument(new ObjectHolderNode(Map.of("testKey", new StringHolderNode("testValue"))), ""),
                result::add);
        assertAll(//
                () -> assertThat(result.size(), equalTo(1)),
                () -> assertThat(result.get(0).get(0).toString(), equalTo("{\"testKey\":\"testValue\"}"))//
        );
    }

    @Test
    void testMapNestedTable() {
        final String nestedListKey = "topics";
        final DocumentPathExpression pathToNestedTable = DocumentPathExpression.builder().addObjectLookup(nestedListKey)
                .addArrayAll().build();
        final PropertyToColumnMapping columnMapping = getColumnMappingExample().varcharColumnSize(200)
                .pathToSourceProperty(pathToNestedTable).build();
        final ColumnMapping indexColumn = new IterationIndexColumnMapping("INDEX",
                DocumentPathExpression.builder().addObjectLookup(nestedListKey).addArrayAll().build());
        final SchemaMappingRequest request = new SchemaMappingRequest(pathToNestedTable,
                List.of(indexColumn, columnMapping));
        final SchemaMapper schemaMapper = new SchemaMapper(request);
        final List<List<ValueExpression>> result = new ArrayList<>();
        schemaMapper.mapRow(
                new FetchedDocument(new ObjectHolderNode(Map.of(nestedListKey,
                        new ArrayHolderNode(
                                List.of(new StringHolderNode("testValue"), new StringHolderNode("testValue"))))),
                        ""),
                result::add);
        assertAll(//
                () -> assertThat(result.size(), equalTo(2)),
                () -> assertThat(result.get(0).get(1).toString(), equalTo("\"testValue\"")), //
                () -> assertThat(((IntegerLiteral) result.get(0).get(0)).getValue(), equalTo(0)),
                () -> assertThat(((IntegerLiteral) result.get(1).get(0)).getValue(), equalTo(1))//
        );
    }
}
