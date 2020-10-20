package com.exasol.adapter.document;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.exasol.ExaConnectionAccessException;
import com.exasol.ExaConnectionInformation;
import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.VirtualSchemaAdapter;
import com.exasol.adapter.capabilities.*;
import com.exasol.adapter.document.mapping.SchemaMapping;
import com.exasol.adapter.document.mapping.SchemaMappingToSchemaMetadataConverter;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
import com.exasol.adapter.document.mapping.reader.JsonSchemaMappingReader;
import com.exasol.adapter.document.mapping.reader.SchemaMappingReader;
import com.exasol.adapter.document.queryplan.QueryPlan;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.document.queryplanning.RemoteTableQueryFactory;
import com.exasol.adapter.metadata.SchemaMetadata;
import com.exasol.adapter.request.*;
import com.exasol.adapter.response.*;
import com.exasol.bucketfs.BucketfsFileFactory;

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
    private static final Set<ScalarFunctionCapability> SUPPORTED_SCALA_FUNCTION_CAPABILITIES = Set.of();

    @Override
    public final CreateVirtualSchemaResponse createVirtualSchema(final ExaMetadata exaMetadata,
            final CreateVirtualSchemaRequest request) throws AdapterException {
        try {
            return runCreateVirtualSchema(exaMetadata, request);
        } catch (final IOException | ExaConnectionAccessException exception) {
            throw new AdapterException("Unable to create Virtual Schema \"" + request.getVirtualSchemaName() + "\". "
                    + "Cause: " + exception.getMessage(), exception);
        }
    }

    private CreateVirtualSchemaResponse runCreateVirtualSchema(final ExaMetadata exaMetadata,
            final CreateVirtualSchemaRequest request)
            throws IOException, AdapterException, ExaConnectionAccessException {
        final SchemaMetadata schemaMetadata = getSchemaMetadata(exaMetadata, request);
        return CreateVirtualSchemaResponse.builder().schemaMetadata(schemaMetadata).build();
    }

    private SchemaMetadata getSchemaMetadata(final ExaMetadata exaMetadata, final AdapterRequest request)
            throws AdapterException, IOException, ExaConnectionAccessException {
        final SchemaMapping schemaMapping = getSchemaMappingDefinition(exaMetadata, request);
        return new SchemaMappingToSchemaMetadataConverter().convert(schemaMapping);
    }

    private SchemaMapping getSchemaMappingDefinition(final ExaMetadata exaMetadata, final AdapterRequest request)
            throws AdapterException, IOException, ExaConnectionAccessException {
        final AdapterProperties adapterProperties = new AdapterProperties(
                request.getSchemaMetadataInfo().getProperties());
        final DynamodbAdapterProperties dynamodbAdapterProperties = new DynamodbAdapterProperties(adapterProperties);
        final File mappingDefinitionFile = getSchemaMappingFile(dynamodbAdapterProperties);
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
     * @throws AdapterException if connection fails
     */
    protected abstract TableKeyFetcher getTableKeyFetcher(ExaConnectionInformation connectionInformation)
            throws AdapterException;

    private ExaConnectionInformation getConnectionInformation(final ExaMetadata exaMetadata,
            final AdapterRequest request) throws ExaConnectionAccessException {
        final AdapterProperties properties = getPropertiesFromRequest(request);
        return exaMetadata.getConnection(properties.getConnectionName());
    }

    private File getSchemaMappingFile(final DynamodbAdapterProperties dynamodbAdapterProperties)
            throws AdapterException {
        final String path = dynamodbAdapterProperties.getMappingDefinition();
        final File file = new BucketfsFileFactory().openFile(path);
        if (!file.exists()) {
            throw new AdapterException("The specified mapping file (" + file
                    + ") could not be found. Make sure you uploaded your mapping definition to BucketFS and specified "
                    + "the correct bucketfs, bucket and path within the bucket.");
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
        try {
            return runPushdown(exaMetadata, request);
        } catch (final ExaConnectionAccessException | IOException exception) {
            throw new AdapterException("Unable to create Virtual Schema \"" + request.getVirtualSchemaName() + "\". "
                    + "Cause: " + exception.getMessage(), exception);
        }
    }

    private PushDownResponse runPushdown(final ExaMetadata exaMetadata, final PushDownRequest request)
            throws AdapterException, ExaConnectionAccessException, IOException {
        final RemoteTableQuery remoteTableQuery = new RemoteTableQueryFactory().build(request.getSelect(),
                request.getSchemaMetadataInfo().getAdapterNotes());
        final String responseStatement = runQuery(exaMetadata, request, remoteTableQuery);
        return PushDownResponse.builder()//
                .pushDownSql(responseStatement)//
                .build();
    }

    private String runQuery(final ExaMetadata exaMetadata, final PushDownRequest request,
            final RemoteTableQuery remoteTableQuery)
            throws ExaConnectionAccessException, IOException, AdapterException {
        final QueryPlanner queryPlanner = getQueryPlanner(getConnectionInformation(exaMetadata, request));
        final AdapterProperties adapterProperties = new AdapterProperties(
                request.getSchemaMetadataInfo().getProperties());
        final int availableClusterCores = getMaxCoreNumber(exaMetadata, adapterProperties);
        final QueryPlan queryPlan = queryPlanner.planQuery(remoteTableQuery, availableClusterCores);
        final String connectionName = getPropertiesFromRequest(request).getConnectionName();
        return new UdfCallBuilder(connectionName, exaMetadata.getScriptSchema(), getAdapterName())
                .getUdfCallSql(queryPlan, remoteTableQuery);
    }

    /**
     * Get an data source specific {@link QueryPlanner}.
     * 
     * @param connectionInformation connection details
     * @return source specific {@link QueryPlanner}
     * @throws AdapterException if connecting fails
     */
    protected abstract QueryPlanner getQueryPlanner(ExaConnectionInformation connectionInformation)
            throws AdapterException;

    /**
     * Get the number of cores that can be used by a query. This methods calculates the number of available cores and
     * limits it by the configured allowed amount.
     *
     * @implNote This method assumes that all cluster nodes have * an equal number of cores.
     *
     * @param exaMetadata       {@link ExaMetadata}
     * @param adapterProperties adapter properties
     * @return total number of cores that are available in the cluster
     */
    private int getMaxCoreNumber(final ExaMetadata exaMetadata, final AdapterProperties adapterProperties)
            throws AdapterException {
        final int cores = Runtime.getRuntime().availableProcessors();
        final DynamodbAdapterProperties dynamodbAdapterProperties = new DynamodbAdapterProperties(adapterProperties);
        final int maxConfiguredCores = dynamodbAdapterProperties.getMaxParallelUdfs();
        return Math.min(((int) exaMetadata.getNodeCount() * cores), maxConfiguredCores);
    }

    @Override
    public final RefreshResponse refresh(final ExaMetadata exaMetadata, final RefreshRequest refreshRequest)
            throws AdapterException {
        try {
            return runRefresh(exaMetadata, refreshRequest);
        } catch (final IOException | ExaConnectionAccessException exception) {
            throw new AdapterException("Unable to update Virtual Schema \"" + refreshRequest.getVirtualSchemaName()
                    + "\". Cause: " + exception.getMessage(), exception);
        }
    }

    private RefreshResponse runRefresh(final ExaMetadata exaMetadata, final RefreshRequest refreshRequest)
            throws IOException, AdapterException, ExaConnectionAccessException {
        final SchemaMetadata schemaMetadata = getSchemaMetadata(exaMetadata, refreshRequest);
        return RefreshResponse.builder().schemaMetadata(schemaMetadata).build();
    }

    @Override
    public final SetPropertiesResponse setProperties(final ExaMetadata exaMetadata,
            final SetPropertiesRequest setPropertiesRequest) {
        throw new UnsupportedOperationException(
                "The current version of this Virtual Schema does not support SET PROPERTIES statement.");
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
                SUPPORTED_SCALA_FUNCTION_CAPABILITIES, "scala-function");
        return GetCapabilitiesResponse.builder().capabilities(capabilities).build();
    }

    private void checkThatCapabilitiesAreSupported(final Set<? extends Enum> actualCapabilities,
            final Set<? extends Enum> supportedCapabilities, final String capabilityType) {
        final List<Enum> unsupportedCapabilities = actualCapabilities.stream()
                .filter(mainCapability -> !supportedCapabilities.contains(mainCapability)).collect(Collectors.toList());
        if (!unsupportedCapabilities.isEmpty()) {
            final String listOfUnsupported = unsupportedCapabilities.stream().map(Enum::toString)
                    .sorted(String::compareTo).collect(Collectors.joining(", "));
            final String listOfSupported = supportedCapabilities.stream().map(Enum::toString).sorted(String::compareTo)
                    .collect(Collectors.joining(", "));
            throw new UnsupportedOperationException(
                    "F-VSD-3 This dialect specified " + capabilityType + "-capabilities (" + listOfUnsupported
                            + ") that are not supported by the abstract DocumentAdapter. "
                            + "Please remove the capability from the specific adapter implementation. " + "Supported "
                            + capabilityType + "-capabilities are [" + listOfSupported + "].");
        }
    }
}
