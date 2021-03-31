package com.exasol.adapter.document;

import static com.exasol.sql.expression.ExpressionTerm.column;
import static com.exasol.utils.StringSerializer.serializeToString;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;
import com.exasol.adapter.document.queryplan.*;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.document.querypredicate.*;
import com.exasol.adapter.metadata.DataType;
import com.exasol.datatype.type.*;
import com.exasol.datatype.type.Boolean;
import com.exasol.errorreporting.ExaError;
import com.exasol.sql.*;
import com.exasol.sql.dql.select.Select;
import com.exasol.sql.dql.select.rendering.SelectRenderer;
import com.exasol.sql.expression.*;
import com.exasol.sql.expression.function.exasol.CastExasolFunction;
import com.exasol.sql.rendering.StringRendererConfig;

/**
 * This class builds push down SQL statement with a UDF call to {@link UdfEntryPoint}.
 * 
 * <p>
 * the push down statement consists of three cascaded statements.
 * 
 * Consider the following example:
 *
 * 
 * SELECT COL1 FROM (
 * 
 * SELECT UDF(PARAMS) EMITS (COL1, COL2) FROM VALUES ((v1, 1), (v2, 2), (v3, 3)) AS P1, C GROUP BY C
 * 
 * ) WHERE COL2 = X
 * </p>
 */
public class UdfCallBuilder {
    private static final String DATA_LOADER_COLUMN = "DATA_LOADER";
    private static final String SCHEMA_MAPPING_REQUEST_COLUMN = "REMOTE_TABLE_QUERY";
    private static final String CONNECTION_NAME_COLUMN = "CONNECTION_NAME";
    private static final String FRAGMENT_ID_COLUMN = "FRAGMENT_ID";
    private final String connectionName;
    private final String adapterSchema;
    private final String adapterName;

    /**
     * Create an instance of {@link UdfCallBuilder}.
     *
     * @param connectionName connectionName that is passed to the UDF
     * @param adapterSchema  schema of the adapter
     * @param adapterName    name of the adapter
     */
    public UdfCallBuilder(final String connectionName, final String adapterSchema, final String adapterName) {
        this.connectionName = connectionName;
        this.adapterSchema = adapterSchema;
        this.adapterName = adapterName;
    }

    /**
     * Build push down SQL statement with a UDF call to {@link UdfEntryPoint}. Each document fetcher gets a row that is
     * passed to a UDF. Since it is not possible to pass data to all UDF calls also the query is added to each row, even
     * though it is the same for all rows.
     * 
     * @param queryPlan plan for the query
     * @param query     document query that is passed to the UDF
     * @return built SQL statement
     */
    public String getUdfCallSql(final QueryPlan queryPlan, final RemoteTableQuery query) {
        final List<ColumnMapping> selectList = query.getSelectList();
        if (queryPlan instanceof EmptyQueryPlan) {
            final Select select = StatementFactory.getInstance().select().all();
            final ValueTableRow.Builder valueTableRow = ValueTableRow.builder(select);
            valueTableRow.add(selectList.stream().map(column -> CastExasolFunction.of(NullLiteral.nullLiteral(),
                    convertDataType(column.getExasolDataType()))).collect(Collectors.toList()));
            select.from().valueTable(new ValueTable(select).appendRow(valueTableRow.build()));
            select.where(BooleanLiteral.of(false));
            return renderStatement(select);
        } else {
            final FetchQueryPlan fetchPlan = (FetchQueryPlan) queryPlan;
            final Select udfCallStatement = buildUdfCallStatement(query, fetchPlan);
            final Select pushDownSelect = wrapStatementInStatementWithPostSelectionAndProjection(selectList,
                    fetchPlan.getPostSelection(), udfCallStatement);
            return renderStatement(pushDownSelect);
        }
    }

    /**
     * Wrap the given {@code SELECT} statement in a new {@code SELECT} statement that adds the post selection as
     * {@code WHERE} clause and the projection as select {@code SELECT} clause.
     *
     * @implNote The post selection can't be applied directly to statement containing the UDF calls as Exasol does not
     *           recognize the column names correctly in the same statement.
     */
    private Select wrapStatementInStatementWithPostSelectionAndProjection(final List<ColumnMapping> selectList,
            final QueryPredicate postSelection, final Select doubleNestedSelect) {
        final Select statement = getSelectForColumns(selectList);
        statement.from().select(doubleNestedSelect);
        final BooleanExpression whereClause = new QueryPredicateToBooleanExpressionConverter().convert(postSelection);
        statement.where(whereClause);
        return statement;
    }

    private Select getSelectForColumns(final List<ColumnMapping> selectList) {
        final String[] selectListStrings = selectList.stream().map(ColumnMapping::getExasolColumnName)
                .toArray(String[]::new);
        return StatementFactory.getInstance().select().field(selectListStrings);
    }

    /**
     * Build the {@code SELECT} statement that contains the call to the UDF and distributes them using a GROUP BY
     * statement.
     */
    private Select buildUdfCallStatement(final RemoteTableQuery query, final FetchQueryPlan queryPlan) {
        final Select udfCallSelect = StatementFactory.getInstance().select();
        final List<ColumnMapping> requiredColumns = getRequiredColumns(query, queryPlan);
        final List<Column> emitsColumns = buildColumnDefinitions(requiredColumns, udfCallSelect);
        udfCallSelect.udf("\"" + this.adapterSchema + "\"." + UdfEntryPoint.UDF_PREFIX + this.adapterName,
                new ColumnsDefinition(emitsColumns), column(DATA_LOADER_COLUMN), column(SCHEMA_MAPPING_REQUEST_COLUMN),
                column(CONNECTION_NAME_COLUMN));
        final SchemaMappingRequest schemaMappingRequest = new SchemaMappingRequest(
                query.getFromTable().getPathInRemoteTable(), requiredColumns);
        final ValueTable valueTable = buildValueTable(queryPlan.getDataLoaders(), schemaMappingRequest, udfCallSelect);
        udfCallSelect.from().valueTableAs(valueTable, "T", DATA_LOADER_COLUMN, SCHEMA_MAPPING_REQUEST_COLUMN,
                CONNECTION_NAME_COLUMN, FRAGMENT_ID_COLUMN);
        udfCallSelect.groupBy(column(FRAGMENT_ID_COLUMN));
        return udfCallSelect;
    }

    private List<ColumnMapping> getRequiredColumns(final RemoteTableQuery query, final FetchQueryPlan queryPlan) {
        final List<ColumnMapping> postSelectionsColumns = new InvolvedColumnCollector()
                .collectInvolvedColumns(queryPlan.getPostSelection());
        return Stream.concat(postSelectionsColumns.stream(), query.getSelectList().stream()).distinct()
                .sorted(Comparator.comparing(ColumnMapping::getExasolColumnName)).collect(Collectors.toList());
    }

    private List<Column> buildColumnDefinitions(final List<ColumnMapping> requiredColumns, final Select udfCallSelect) {
        return requiredColumns.stream().map(column -> new Column(udfCallSelect, column.getExasolColumnName(),
                convertDataType(column.getExasolDataType()))).collect(Collectors.toList());
    }

    private ValueTable buildValueTable(final List<DataLoader> dataLoaders,
            final SchemaMappingRequest schemaMappingRequest, final Select select) {
        final ValueTable valueTable = new ValueTable(select);
        int rowCounter = 0;
        final String serializedSchemaMappingRequest = serializeSchemaMappingRequest(schemaMappingRequest);
        for (final DataLoader dataLoader : dataLoaders) {
            final String serializedDataLoader = serializeDataLoader(dataLoader);
            final ValueTableRow row = ValueTableRow.builder(select).add(serializedDataLoader)
                    .add(serializedSchemaMappingRequest) //
                    .add(this.connectionName) //
                    .add(rowCounter) //
                    .build();
            valueTable.appendRow(row);
            ++rowCounter;
        }
        return valueTable;
    }

    private String serializeDataLoader(final DataLoader dataLoader) {
        try {
            return serializeToString(dataLoader);
        } catch (final IOException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-19")
                    .message("Internal error (Failed to serialize DataLoader).").ticketMitigation().toString(),
                    exception);
        }
    }

    private String serializeSchemaMappingRequest(final SchemaMappingRequest schemaMappingRequest) {
        try {
            return serializeToString(schemaMappingRequest);
        } catch (final IOException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-18")
                    .message("Internal error (Failed to serialize SchemaMappingRequest).").ticketMitigation()
                    .toString(), exception);
        }
    }

    private com.exasol.datatype.type.DataType convertDataType(final DataType adapterDataType) {
        switch (adapterDataType.getExaDataType()) {
        case DECIMAL:
            return new Decimal(adapterDataType.getPrecision(), adapterDataType.getScale());
        case DOUBLE:
            return new DoublePrecision();
        case VARCHAR:
            return new Varchar(adapterDataType.getSize());
        case CHAR:
            return new Char(adapterDataType.getSize());
        case DATE:
            return new Date();
        case TIMESTAMP:
            return adapterDataType.isWithLocalTimezone() ? new TimestampWithLocalTimezone() : new Timestamp();
        case BOOLEAN:
            return new Boolean();
        default:
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-69")
                    .message("Unimplemented conversion of type {{TYPE}}.")
                    .parameter("TYPE", adapterDataType.getExaDataType().toString()).ticketMitigation().toString());
        }
    }

    private String renderStatement(final Select pushDownSelect) {
        final StringRendererConfig config = StringRendererConfig.builder().quoteIdentifiers(true).build();
        final SelectRenderer renderer = new SelectRenderer(config);
        pushDownSelect.accept(renderer);
        return renderer.render();
    }
}
