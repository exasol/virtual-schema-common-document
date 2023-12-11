package com.exasol.adapter.document;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.*;
import com.exasol.adapter.document.queryplan.*;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.document.querypredicate.*;
import com.exasol.adapter.sql.SqlLiteralString;

class UdfCallBuilderTest {
    private static final String CONNECTION = "MY_CONNECTION";
    private static final String ADAPTER_SCHEMA = "ADAPTERS";
    private static final String TEST_ADAPTER = "TEST_ADAPTER";
    private static final String QUOTED_STRING_REGEX = "'[^']+'";
    private static final UdfCallBuilder UDF_CALL_BUILDER = new UdfCallBuilder(CONNECTION, ADAPTER_SCHEMA, TEST_ADAPTER);

    @Test
    void testBuildForEmptyPlan() {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryWithOneColumn();
        final QueryPlan queryPlan = new EmptyQueryPlan();
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql, equalTo("SELECT * FROM (VALUES (CAST(NULL AS  VARCHAR(123)))) WHERE FALSE"));
    }

    @ParameterizedTest
    @MethodSource("columnTypes")
    void testBuildForEmptyPlanWithDataTypes(final ColumnMapping column, final String expectedCastType) {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryWithOneColumn(column);
        final QueryPlan queryPlan = new EmptyQueryPlan();
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql, equalTo("SELECT * FROM (VALUES (CAST(NULL AS  " + expectedCastType + "))) WHERE FALSE"));
    }

    static Stream<Arguments> columnTypes() {
        final String colName = "TEST_COLUMN";
        return Stream.of(
                Arguments.of(PropertyToTimestampColumnMapping.builder().exasolColumnName(colName).build(), "TIMESTAMP"),
                Arguments.of(PropertyToDateColumnMapping.builder().exasolColumnName(colName).build(), "DATE"),
                Arguments.of(
                        PropertyToJsonColumnMapping.builder().exasolColumnName(colName).varcharColumnSize(5).build(),
                        "VARCHAR(5)"),
                Arguments.of(PropertyToBoolColumnMapping.builder().exasolColumnName(colName).build(), "BOOLEAN"),
                Arguments.of(PropertyToDecimalColumnMapping.builder().exasolColumnName(colName).decimalPrecision(5)
                        .decimalScale(3).build(), "DECIMAL(5,3)"),
                Arguments.of(PropertyToDoubleColumnMapping.builder().exasolColumnName(colName).build(),
                        "DOUBLE PRECISION"),
                Arguments.of(
                        PropertyToVarcharColumnMapping.builder().exasolColumnName(colName).varcharColumnSize(5).build(),
                        "VARCHAR(5)"));
    }

    @Test
    void testBasicSqlBuilding() {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryWithOneColumn();
        final FetchQueryPlan queryPlan = new FetchQueryPlan(List.of(), new NoPredicate());
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql,
                matchesRegex(quoteRegex(
                        "SELECT \"TEST_COLUMN\" FROM (SELECT \"ADAPTERS\".IMPORT_FROM_TEST_ADAPTER(\"DATA_LOADER\", ")
                        + QUOTED_STRING_REGEX
                        + quoteRegex(", 'MY_CONNECTION') EMITS (\"TEST_COLUMN\" VARCHAR(123))"
                                + " FROM (VALUES ) AS \"T\"(\"DATA_LOADER\", \"FRAGMENT_ID\")"
                                + " GROUP BY \"FRAGMENT_ID\") WHERE TRUE")));
    }

    @ParameterizedTest
    @MethodSource("columnTypes")
    void testBasicSqlBuildingWithDataTypes(final ColumnMapping column, final String expectedUdfEmitType) {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryWithOneColumn(column);
        final FetchQueryPlan queryPlan = new FetchQueryPlan(List.of(), new NoPredicate());
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql,
                matchesRegex(quoteRegex(
                        "SELECT \"TEST_COLUMN\" FROM (SELECT \"ADAPTERS\".IMPORT_FROM_TEST_ADAPTER(\"DATA_LOADER\", ")
                        + QUOTED_STRING_REGEX
                        + quoteRegex(", 'MY_CONNECTION') EMITS (\"TEST_COLUMN\" " + expectedUdfEmitType
                                + ") FROM (VALUES ) AS \"T\"(\"DATA_LOADER\", \"FRAGMENT_ID\")"
                                + " GROUP BY \"FRAGMENT_ID\") WHERE TRUE")));
    }

    /**
     * Quote the given regular expression pattern by enclosing it in {@code \Q...\E}, so that you don't need to quote
     * all special characters like {@code ()}.
     * 
     * @param pattern the pattern to quote
     * @return the quoted pattern
     */
    private String quoteRegex(final String pattern) {
        return "\\Q" + pattern + "\\E";
    }

    @Test
    void testAddPostSelection() {
        final RemoteTableQuery remoteTableQuery = getRemoteTableQueryWithOneColumn();
        final ColumnLiteralComparisonPredicate postSelection = new ColumnLiteralComparisonPredicate(
                AbstractComparisonPredicate.Operator.EQUAL, new SourceReferenceColumnMapping(),
                new SqlLiteralString("testValue"));
        final FetchQueryPlan queryPlan = new FetchQueryPlan(List.of(), postSelection);
        final String udfCallSql = UDF_CALL_BUILDER.getUdfCallSql(queryPlan, remoteTableQuery);
        assertThat(udfCallSql, matchesRegex( //
                quoteRegex(
                        "SELECT \"TEST_COLUMN\" FROM (SELECT \"ADAPTERS\".IMPORT_FROM_TEST_ADAPTER(\"DATA_LOADER\", ")
                        + QUOTED_STRING_REGEX
                        + quoteRegex(
                                ", 'MY_CONNECTION') EMITS (\"SOURCE_REFERENCE\" VARCHAR(2000), \"TEST_COLUMN\" VARCHAR(123))"
                                        + " FROM (VALUES ) AS \"T\"(\"DATA_LOADER\", \"FRAGMENT_ID\")"
                                        + " GROUP BY \"FRAGMENT_ID\")" //
                                        + " WHERE \"SOURCE_REFERENCE\" = 'testValue'")));
    }

    private RemoteTableQuery getRemoteTableQueryWithOneColumn() {
        final ColumnMapping column = PropertyToJsonColumnMapping.builder().exasolColumnName("TEST_COLUMN")
                .varcharColumnSize(123).build();
        return getRemoteTableQueryWithOneColumn(column);
    }

    private RemoteTableQuery getRemoteTableQueryWithOneColumn(final ColumnMapping column) {
        final TableMapping tableMapping = new TableMapping("TEST", "test", List.of(column),
                DocumentPathExpression.empty(), null);
        return new RemoteTableQuery(tableMapping, List.of(column), new NoPredicate());
    }
}
