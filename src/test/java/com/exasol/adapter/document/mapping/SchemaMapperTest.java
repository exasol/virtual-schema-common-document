package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.getColumnMappingExample;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentfetcher.FetchedDocument;
import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.MockArrayNode;
import com.exasol.adapter.document.documentnode.MockObjectNode;
import com.exasol.adapter.document.documentnode.MockValueNode;
import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.sql.expression.IntegerLiteral;
import com.exasol.sql.expression.StringLiteral;
import com.exasol.sql.expression.ValueExpression;

class SchemaMapperTest {
    private static final StringLiteral STRING_LITERAL = StringLiteral.of("test");

    @Test
    void testMapRow() {
        final PropertyToColumnMapping columnMapping = getColumnMappingExample().build();
        final SchemaMappingRequest request = new SchemaMappingRequest(DocumentPathExpression.empty(),
                List.of(columnMapping));
        final SchemaMapper<Object> schemaMapper = new SchemaMapper<>(request,
                new MockPropertyToColumnValueExtractorFactory());
        final List<List<ValueExpression>> result = schemaMapper.mapRow(
                new FetchedDocument<>(new MockObjectNode(Map.of("testKey", new MockValueNode("testValue"))), ""))
                .collect(Collectors.toList());
        assertAll(//
                () -> assertThat(result.size(), equalTo(1)),
                () -> assertThat(result.get(0), containsInAnyOrder(STRING_LITERAL))//
        );
    }

    @Test
    void testMapNestedTable() {
        final String nestedListKey = "topics";
        final DocumentPathExpression pathToNestedTable = DocumentPathExpression.builder().addObjectLookup(nestedListKey)
                .addArrayAll().build();
        final PropertyToColumnMapping columnMapping = getColumnMappingExample().pathToSourceProperty(pathToNestedTable)
                .build();
        final ColumnMapping indexColumn = new IterationIndexColumnMapping("INDEX",
                DocumentPathExpression.builder().addObjectLookup(nestedListKey).addArrayAll().build());
        final SchemaMappingRequest request = new SchemaMappingRequest(pathToNestedTable,
                List.of(indexColumn, columnMapping));
        final SchemaMapper<Object> schemaMapper = new SchemaMapper<>(request,
                new MockPropertyToColumnValueExtractorFactory());
        final List<List<ValueExpression>> result = schemaMapper.mapRow(new FetchedDocument<>(
                new MockObjectNode(Map.of(nestedListKey,
                        new MockArrayNode(List.of(new MockValueNode("testValue"), new MockValueNode("testValue"))))),
                "")).collect(Collectors.toList());
        assertAll(//
                () -> assertThat(result.size(), equalTo(2)),
                () -> assertThat(result.get(0).get(1), equalTo(STRING_LITERAL)), //
                () -> assertThat(((IntegerLiteral) result.get(0).get(0)).getValue(), equalTo(0)),
                () -> assertThat(((IntegerLiteral) result.get(1).get(0)).getValue(), equalTo(1))//
        );
    }

    private static class MockPropertyToColumnValueExtractorFactory
            implements PropertyToColumnValueExtractorFactory<Object> {

        @Override
        public ColumnValueExtractor<Object> getValueExtractorForColumn(final PropertyToColumnMapping column) {
            return new MockValueMapper(column);
        }
    }

    private static class MockValueMapper extends AbstractPropertyToColumnValueExtractor<Object> {

        public MockValueMapper(final PropertyToColumnMapping column) {
            super(column);
        }

        @Override
        protected ValueExpression mapValue(final DocumentNode<Object> documentValue) {
            return STRING_LITERAL;
        }
    }
}
