package com.exasol.adapter.document;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.exasol.*;
import com.exasol.adapter.*;
import com.exasol.adapter.capabilities.*;
import com.exasol.adapter.document.connection.ConnectionPropertiesReader;
import com.exasol.adapter.document.connection.ConnectionStringReader;
import com.exasol.adapter.document.mapping.*;
import com.exasol.adapter.document.mapping.reader.JsonSchemaMappingReader;
import com.exasol.adapter.document.properties.DocumentAdapterProperties;
import com.exasol.adapter.document.queryplan.QueryPlan;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.document.queryplanning.RemoteTableQueryFactory;
import com.exasol.adapter.metadata.SchemaMetadata;
import com.exasol.adapter.request.*;
import com.exasol.adapter.response.*;
import com.exasol.adapter.sql.SqlStatement;
import com.exasol.errorreporting.ExaError;

/**
 * This class is the basis for Virtual Schema adapter for document data.
 */
public class DocumentAdapter implements VirtualSchemaAdapter {
    private static final Set<MainCapability> SUPPORTED_MAIN_CAPABILITIES = Set.of(MainCapability.SELECTLIST_PROJECTION,
            MainCapability.FILTER_EXPRESSIONS);
    private static final Set<PredicateCapability> SUPPORTED_PREDICATE_CAPABILITIES = Set.of(PredicateCapability.EQUAL,
            PredicateCapability.NOTEQUAL, PredicateCapability.LESS, PredicateCapability.LESSEQUAL,
            PredicateCapability.LIKE, PredicateCapability.LIKE_ESCAPE, PredicateCapability.AND, PredicateCapability.OR,
            PredicateCapability.NOT);
    private static final Set<LiteralCapability> SUPPORTED_LITERAL_CAPABILITIES = Set.of(LiteralCapability.STRING,
            LiteralCapability.NULL, LiteralCapability.BOOL, LiteralCapability.DOUBLE, LiteralCapability.EXACTNUMERIC);
    private static final Set<AggregateFunctionCapability> SUPPORTED_AGGREGATE_FUNCTION_CAPABILITIES = Set.of();
    private static final Set<ScalarFunctionCapability> SUPPORTED_SCALAR_FUNCTION_CAPABILITIES = Set.of();
    private final int thisNodesCoreCount;
    private final DocumentAdapterDialect dialect;

    /**
     * Create a new instance of {@link DocumentAdapter}.
     * 
     * @param dialect dialect implementation
     */
    public DocumentAdapter(final DocumentAdapterDialect dialect) {
        this.dialect = dialect;
        this.thisNodesCoreCount = Runtime.getRuntime().availableProcessors();
    }

    @Override
    public final CreateVirtualSchemaResponse createVirtualSchema(final ExaMetadata exaMetadata,
            final CreateVirtualSchemaRequest request) {
        final SchemaMetadata schemaMetadata = getSchemaMetadata(exaMetadata, request);
        return CreateVirtualSchemaResponse.builder().schemaMetadata(schemaMetadata).build();
    }

    private SchemaMetadata getSchemaMetadata(final ExaMetadata exaMetadata, final AdapterRequest request) {
        final SchemaMapping schemaMapping = getSchemaMappingDefinition(exaMetadata, request);
        return new SchemaMappingToSchemaMetadataConverter().convert(schemaMapping);
    }

    private SchemaMapping getSchemaMappingDefinition(final ExaMetadata exaMetadata, final AdapterRequest request) {
        final AdapterProperties adapterProperties = new AdapterProperties(
                request.getSchemaMetadataInfo().getProperties());
        final DocumentAdapterProperties documentAdapterProperties = new DocumentAdapterProperties(adapterProperties);
        getConnectionInformation(exaMetadata, request);
        final TableKeyFetcher tableKeyFetcher = this.dialect
                .getTableKeyFetcher(getConnectionInformation(exaMetadata, request));
        return new JsonSchemaMappingReader(tableKeyFetcher)
                .readSchemaMapping(documentAdapterProperties.getMappingDefinition());
    }

    private ConnectionPropertiesReader getConnectionInformation(final ExaMetadata exaMetadata,
            final AdapterRequest request) {
        try {
            final String userGuideUrl = this.dialect.getUserGuideUrl();
            final AdapterProperties properties = getPropertiesFromRequest(request);
            final ExaConnectionInformation connection = exaMetadata.getConnection(properties.getConnectionName());
            final String connectionString = new ConnectionStringReader(userGuideUrl).read(connection);
            return new ConnectionPropertiesReader(connectionString, userGuideUrl);
        } catch (final ExaConnectionAccessException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-VSD-15")
                            .message("Could not access the remote databases connection information.").toString(),
                    exception);
        }
    }

    private AdapterProperties getPropertiesFromRequest(final AdapterRequest request) {
        return new AdapterProperties(request.getSchemaMetadataInfo().getProperties());
    }

    @Override
    public final DropVirtualSchemaResponse dropVirtualSchema(final ExaMetadata exaMetadata,
            final DropVirtualSchemaRequest dropVirtualSchemaRequest) {
        return DropVirtualSchemaResponse.builder().build();
    }

    /**
     * Runs the actual query. The data is fetched using a scan from DynamoDB and then transformed into a
     * {@code SELECT FROM VALUES} statement and passed back to Exasol.
     */
    @Override
    public final PushDownResponse pushdown(final ExaMetadata exaMetadata, final PushDownRequest request)
            throws AdapterException {
        final SqlStatement sqlQuery = request.getSelect();
        final String adapterNotes = request.getSchemaMetadataInfo().getAdapterNotes();
        final RemoteTableQuery remoteTableQuery = new RemoteTableQueryFactory().build(sqlQuery, adapterNotes);
        final String responseStatement = runQuery(exaMetadata, request, remoteTableQuery);
        return PushDownResponse.builder()//
                .pushDownSql(responseStatement)//
                .build();
    }

    private String runQuery(final ExaMetadata exaMetadata, final PushDownRequest request,
            final RemoteTableQuery remoteTableQuery) {
        final AdapterProperties adapterProperties = new AdapterProperties(
                request.getSchemaMetadataInfo().getProperties());
        final QueryPlanner queryPlanner = this.dialect.getQueryPlanner(getConnectionInformation(exaMetadata, request),
                adapterProperties);
        final DocumentAdapterProperties documentAdapterProperties = new DocumentAdapterProperties(adapterProperties);
        final int availableClusterCores = new UdfCountCalculator().calculateMaxUdfInstanceCount(exaMetadata,
                documentAdapterProperties, this.thisNodesCoreCount);
        final QueryPlan queryPlan = queryPlanner.planQuery(remoteTableQuery, availableClusterCores);
        final String connectionName = getPropertiesFromRequest(request).getConnectionName();
        return new UdfCallBuilder(connectionName, exaMetadata.getScriptSchema(), this.dialect.getAdapterName())
                .getUdfCallSql(queryPlan, remoteTableQuery);
    }

    @Override
    public final RefreshResponse refresh(final ExaMetadata exaMetadata, final RefreshRequest refreshRequest)
            throws AdapterException {
        final SchemaMetadata schemaMetadata = getSchemaMetadata(exaMetadata, refreshRequest);
        return RefreshResponse.builder().schemaMetadata(schemaMetadata).build();
    }

    @Override
    public final SetPropertiesResponse setProperties(final ExaMetadata exaMetadata,
            final SetPropertiesRequest setPropertiesRequest) {
        throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-27")
                .message("The current version of this Virtual Schema does not support SET PROPERTIES statement.")
                .mitigation("Drop and recreate the virtual schema instead.").toString());
    }

    @Override
    public final GetCapabilitiesResponse getCapabilities(final ExaMetadata metadata,
            final GetCapabilitiesRequest request) throws AdapterException {
        final Capabilities capabilities = this.dialect.getCapabilities();
        checkThatCapabilitiesAreSupported(capabilities.getMainCapabilities(), SUPPORTED_MAIN_CAPABILITIES, "main");
        checkThatCapabilitiesAreSupported(capabilities.getPredicateCapabilities(), SUPPORTED_PREDICATE_CAPABILITIES,
                "predicate");
        checkThatCapabilitiesAreSupported(capabilities.getLiteralCapabilities(), SUPPORTED_LITERAL_CAPABILITIES,
                "literal");
        checkThatCapabilitiesAreSupported(capabilities.getAggregateFunctionCapabilities(),
                SUPPORTED_AGGREGATE_FUNCTION_CAPABILITIES, "aggregate-function");
        checkThatCapabilitiesAreSupported(capabilities.getScalarFunctionCapabilities(),
                SUPPORTED_SCALAR_FUNCTION_CAPABILITIES, "scalar-function");
        return GetCapabilitiesResponse.builder().capabilities(capabilities).build();
    }

    private void checkThatCapabilitiesAreSupported(final Set<? extends Enum<?>> actualCapabilities,
            final Set<? extends Enum<?>> supportedCapabilities, final String capabilityType) {
        final List<Enum<?>> unsupportedCapabilities = actualCapabilities.stream()
                .filter(mainCapability -> !supportedCapabilities.contains(mainCapability)).collect(Collectors.toList());
        if (!unsupportedCapabilities.isEmpty()) {
            final String listOfUnsupported = unsupportedCapabilities.stream().map(Enum::toString)
                    .sorted(String::compareTo).collect(Collectors.joining(", "));
            final String listOfSupported = supportedCapabilities.stream().map(Enum::toString).sorted(String::compareTo)
                    .collect(Collectors.joining(", "));
            throw new UnsupportedOperationException(ExaError.messageBuilder("F-VSD-3").message(
                    "This dialect specified {{capabilityType|uq}}-capabilities ({{listOfUnsupported}}) that are not supported by the abstract DocumentAdapter.")
                    .parameter("listOfUnsupported", listOfUnsupported)
                    .mitigation("Please remove the capability from the specific adapter implementation. "
                            + "Supported {{capabilityType|uq}}-capabilities are [{{listOfSupported}}].")
                    .parameter("capabilityType", capabilityType).parameter("listOfSupported", listOfSupported)
                    .toString());
        }
    }
}
