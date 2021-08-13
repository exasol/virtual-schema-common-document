package com.exasol.adapter.document;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.exasol.*;
import com.exasol.adapter.*;
import com.exasol.adapter.capabilities.*;
import com.exasol.adapter.document.mapping.*;
import com.exasol.adapter.document.mapping.reader.JsonSchemaMappingReader;
import com.exasol.adapter.document.mapping.reader.SchemaMappingReader;
import com.exasol.adapter.document.queryplan.QueryPlan;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.document.queryplanning.RemoteTableQueryFactory;
import com.exasol.adapter.metadata.SchemaMetadata;
import com.exasol.adapter.request.*;
import com.exasol.adapter.response.*;
import com.exasol.adapter.sql.SqlStatement;
import com.exasol.bucketfs.BucketfsFileFactory;
import com.exasol.errorreporting.ExaError;

/**
 * This class is the abstract basis for Virtual Schema adapter for document data.
 */
public abstract class DocumentAdapter implements VirtualSchemaAdapter {
    private static final Set<MainCapability> SUPPORTED_MAIN_CAPABILITIES = Set.of(MainCapability.SELECTLIST_PROJECTION,
            MainCapability.FILTER_EXPRESSIONS);
    private static final Set<PredicateCapability> SUPPORTED_PREDICATE_CAPABILITIES = Set.of(PredicateCapability.EQUAL,
            PredicateCapability.NOTEQUAL, PredicateCapability.LESS, PredicateCapability.LESSEQUAL,
            PredicateCapability.LIKE, PredicateCapability.AND, PredicateCapability.OR, PredicateCapability.NOT);
    private static final Set<LiteralCapability> SUPPORTED_LITERAL_CAPABILITIES = Set.of(LiteralCapability.STRING,
            LiteralCapability.NULL, LiteralCapability.BOOL, LiteralCapability.DOUBLE, LiteralCapability.EXACTNUMERIC);
    private static final Set<AggregateFunctionCapability> SUPPORTED_AGGREGATE_FUNCTION_CAPABILITIES = Set.of();
    private static final Set<ScalarFunctionCapability> SUPPORTED_SCALAR_FUNCTION_CAPABILITIES = Set.of();
    private final int thisNodesCoreCount;

    protected DocumentAdapter() {
        this.thisNodesCoreCount = Runtime.getRuntime().availableProcessors();
    }

    @Override
    public final CreateVirtualSchemaResponse createVirtualSchema(final ExaMetadata exaMetadata,
            final CreateVirtualSchemaRequest request) throws AdapterException {
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
        final File mappingDefinitionFile = getSchemaMappingFile(documentAdapterProperties);
        getConnectionInformation(exaMetadata, request);
        final TableKeyFetcher tableKeyFetcher = getTableKeyFetcher(getConnectionInformation(exaMetadata, request));
        final SchemaMappingReader mappingFactory = new JsonSchemaMappingReader(mappingDefinitionFile, tableKeyFetcher);
        return mappingFactory.getSchemaMapping();
    }

    /**
     * Get a database specific {@link TableKeyFetcher}.
     * 
     * @param connectionInformation connection details
     * @return database specific {@link TableKeyFetcher}
     */
    protected abstract TableKeyFetcher getTableKeyFetcher(ExaConnectionInformation connectionInformation);

    private ExaConnectionInformation getConnectionInformation(final ExaMetadata exaMetadata,
            final AdapterRequest request) {
        try {
            final AdapterProperties properties = getPropertiesFromRequest(request);
            return exaMetadata.getConnection(properties.getConnectionName());
        } catch (final ExaConnectionAccessException exception) {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-VSD-15")
                            .message("Could not access the remote databases connection information.").toString(),
                    exception);
        }
    }

    private File getSchemaMappingFile(final DocumentAdapterProperties documentAdapterProperties) {
        final String path = documentAdapterProperties.getMappingDefinition();
        final File file = new BucketfsFileFactory().openFile(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-14")
                    .message("Could not open mapping file {{MAPPING_FILE}}.").parameter("MAPPING_FILE", file)
                    .mitigation(
                            "Make sure you uploaded your mapping definition to BucketFS and specified the correct BucketFS, bucket and path within the bucket.")
                    .toString());
        }
        return file;
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
        final QueryPlanner queryPlanner = getQueryPlanner(getConnectionInformation(exaMetadata, request),
                adapterProperties);
        final DocumentAdapterProperties documentAdapterProperties = new DocumentAdapterProperties(adapterProperties);
        final int availableClusterCores = new UdfCountCalculator().calculateMaxUdfInstanceCount(exaMetadata,
                documentAdapterProperties, this.thisNodesCoreCount);
        final QueryPlan queryPlan = queryPlanner.planQuery(remoteTableQuery, availableClusterCores);
        final String connectionName = getPropertiesFromRequest(request).getConnectionName();
        return new UdfCallBuilder(connectionName, exaMetadata.getScriptSchema(), getAdapterName())
                .getUdfCallSql(queryPlan, remoteTableQuery);
    }

    /**
     * Get an data source specific {@link QueryPlanner}.
     * 
     * @param connectionInformation connection details
     * @param adapterProperties     adapter properties
     * @return source specific {@link QueryPlanner}
     */
    protected abstract QueryPlanner getQueryPlanner(ExaConnectionInformation connectionInformation,
            AdapterProperties adapterProperties);

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

    /**
     * Get the name of the database-specific adapter.
     * 
     * @return name of the database-specific adapter
     */
    protected abstract String getAdapterName();

    protected abstract Capabilities getCapabilities();

    @Override
    public final GetCapabilitiesResponse getCapabilities(final ExaMetadata metadata,
            final GetCapabilitiesRequest request) throws AdapterException {
        final Capabilities capabilities = getCapabilities();
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
            throw new UnsupportedOperationException(
                    "F-VSD-3: This dialect specified " + capabilityType + "-capabilities (" + listOfUnsupported
                            + ") that are not supported by the abstract DocumentAdapter. "
                            + "Please remove the capability from the specific adapter implementation. " + "Supported "
                            + capabilityType + "-capabilities are [" + listOfSupported + "].");
        }
    }
}
