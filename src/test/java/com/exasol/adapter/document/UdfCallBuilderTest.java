package com.exasol.adapter.document;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.SourceReferenceColumnMapping;
import com.exasol.adapter.document.mapping.TableMapping;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.document.querypredicate.AbstractComparisonPredicate;
import com.exasol.adapter.document.querypredicate.ColumnLiteralComparisonPredicate;
import com.exasol.adapter.document.querypredicate.NoPredicate;
import com.exasol.adapter.sql.SqlLiteralString;

class UdfCallBuilderTest {
    private static final String CONNECTION = "MY_CONNECTION";
    private static final String ADAPTER_SCHEMA = "ADAPTERS";
    private static final String TEST_ADAPTER = "TEST_ADAPTER";
    private static final UdfCallBuilder UDF_CALL_BUILDER = new UdfCallBuilder(CONNECTION, ADAPTER_SCHEMA, TEST_ADAPTER);

    @Test
    void testBasicSqlBuilding() throws IOException {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryForNoColumns();
        final QueryPlan queryPlan = new QueryPlan(List.of(), new NoPredicate());
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql, equalTo(
                "SELECT  FROM (SELECT \"ADAPTERS\".IMPORT_FROM_TEST_ADAPTER(\"DATA_LOADER\", \"REMOTE_TABLE_QUERY\", \"CONNECTION_NAME\") EMITS  FROM (VALUES ) AS \"T\"(\"DATA_LOADER\", \"REMOTE_TABLE_QUERY\", \"CONNECTION_NAME\", \"FRAGMENT_ID\") GROUP BY \"FRAGMENT_ID\") WHERE TRUE"));
    }

    @Test
    void testAddPostSelection() throws IOException {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryForNoColumns();
        final ColumnLiteralComparisonPredicate postSelection = new ColumnLiteralComparisonPredicate(
                AbstractComparisonPredicate.Operator.EQUAL, new SourceReferenceColumnMapping(),
                new SqlLiteralString("testValue"));
        final QueryPlan queryPlan = new QueryPlan(List.of(), postSelection);
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql, equalTo(
                "SELECT  FROM (SELECT \"ADAPTERS\".IMPORT_FROM_TEST_ADAPTER(\"DATA_LOADER\", \"REMOTE_TABLE_QUERY\", \"CONNECTION_NAME\") EMITS (\"SOURCE_REFERENCE\" VARCHAR(2000)) FROM (VALUES ) AS \"T\"(\"DATA_LOADER\", \"REMOTE_TABLE_QUERY\", \"CONNECTION_NAME\", \"FRAGMENT_ID\") GROUP BY \"FRAGMENT_ID\") WHERE \"SOURCE_REFERENCE\" = 'testValue'"));
    }

    private RemoteTableQuery getRemoteTableQueryForNoColumns() {
        final TableMapping tableMapping = new TableMapping("TEST", "test", Collections.emptyList(),
                DocumentPathExpression.empty());
        return new RemoteTableQuery(tableMapping, Collections.emptyList(), new NoPredicate());
    }
}