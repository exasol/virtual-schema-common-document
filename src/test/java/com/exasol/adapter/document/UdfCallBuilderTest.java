package com.exasol.adapter.document;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.SourceReferenceColumnMapping;
import com.exasol.adapter.document.mapping.TableMapping;
import com.exasol.adapter.document.queryplan.EmptyQueryPlan;
import com.exasol.adapter.document.queryplan.FetchQueryPlan;
import com.exasol.adapter.document.queryplan.QueryPlan;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate;
import com.exasol.adapter.document.querypredicate.ColumnLiteralComparisonPredicate;
import com.exasol.adapter.document.querypredicate.NoPredicate;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.sql.SqlLiteralString;

class UdfCallBuilderTest {
    private static final String CONNECTION = "MY_CONNECTION";
    private static final String ADAPTER_SCHEMA = "ADAPTERS";
    private static final String TEST_ADAPTER = "TEST_ADAPTER";
    private static final UdfCallBuilder UDF_CALL_BUILDER = new UdfCallBuilder(CONNECTION, ADAPTER_SCHEMA, TEST_ADAPTER);

    @Test
    void testBuildForEmptyPlan() throws IOException {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryWithOneColumns();
        final QueryPlan queryPlan = new EmptyQueryPlan();
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql, equalTo("SELECT * FROM (VALUES (CAST(NULL AS DECIMAL(10,2)))) WHERE FALSE"));
    }

    @Test
    void testBasicSqlBuilding() throws IOException {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryWithOneColumns();
        final FetchQueryPlan queryPlan = new FetchQueryPlan(List.of(), new NoPredicate());
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql, equalTo(
                "SELECT \"TEST_COLUMN\" FROM (SELECT \"ADAPTERS\".IMPORT_FROM_TEST_ADAPTER(\"DATA_LOADER\", \"REMOTE_TABLE_QUERY\", \"CONNECTION_NAME\") EMITS (\"TEST_COLUMN\" DECIMAL(10,2)) FROM (VALUES ) AS \"T\"(\"DATA_LOADER\", \"REMOTE_TABLE_QUERY\", \"CONNECTION_NAME\", \"FRAGMENT_ID\") GROUP BY \"FRAGMENT_ID\") WHERE TRUE"));
    }

    @Test
    void testAddPostSelection() throws IOException {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryWithOneColumns();
        final ColumnLiteralComparisonPredicate postSelection = new ColumnLiteralComparisonPredicate(
                AbstractComparisonPredicate.Operator.EQUAL, new SourceReferenceColumnMapping(),
                new SqlLiteralString("testValue"));
        final FetchQueryPlan queryPlan = new FetchQueryPlan(List.of(), postSelection);
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql, equalTo(
                "SELECT \"TEST_COLUMN\" FROM (SELECT \"ADAPTERS\".IMPORT_FROM_TEST_ADAPTER(\"DATA_LOADER\", \"REMOTE_TABLE_QUERY\", \"CONNECTION_NAME\") EMITS (\"SOURCE_REFERENCE\" VARCHAR(2000), \"TEST_COLUMN\" DECIMAL(10,2)) FROM (VALUES ) AS \"T\"(\"DATA_LOADER\", \"REMOTE_TABLE_QUERY\", \"CONNECTION_NAME\", \"FRAGMENT_ID\") GROUP BY \"FRAGMENT_ID\") WHERE \"SOURCE_REFERENCE\" = 'testValue'"));
    }

    private RemoteTableQuery getRemoteTableQueryWithOneColumns() {
        final ColumnMapping column = mock(ColumnMapping.class);
        when(column.getExasolColumnName()).thenAnswer(interaction -> "TEST_COLUMN");
        when(column.getExasolDataType()).thenAnswer(interaction -> DataType.createDecimal(10, 2));
        final TableMapping tableMapping = new TableMapping("TEST", "test", List.of(column),
                DocumentPathExpression.empty());
        return new RemoteTableQuery(tableMapping, List.of(column), new NoPredicate());
    }
}