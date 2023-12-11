package com.exasol.adapter.document;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.*;
import com.exasol.adapter.*;
import com.exasol.adapter.capabilities.*;
import com.exasol.adapter.document.connection.ConnectionPropertiesReader;
import com.exasol.adapter.document.connection.ConnectionStringReader;
import com.exasol.adapter.document.mapping.*;
import com.exasol.adapter.document.mapping.auto.SchemaFetcher;
import com.exasol.adapter.document.mapping.auto.SchemaInferencer;
import com.exasol.adapter.document.mapping.reader.JsonSchemaMappingReader;
import com.exasol.adapter.document.properties.DocumentAdapterProperties;
import com.exasol.adapter.document.properties.EdmlInput;
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
    private static final Logger LOG = Logger.getLogger(DocumentAdapter.class.getName());
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
        final SchemaMetadata schemaMetadata = getSchemaMetadata(exaMetadata, getPropertiesFromRequest(request));
        return CreateVirtualSchemaResponse.builder().schemaMetadata(schemaMetadata).build();
    }

    private SchemaMetadata getSchemaMetadata(final ExaMetadata exaMetadata, final AdapterProperties adapterProperties) {
        final SchemaMapping schemaMapping = getSchemaMappingDefinition(exaMetadata, adapterProperties);
        return new SchemaMappingToSchemaMetadataConverter().convert(schemaMapping);
    }

    private SchemaMapping getSchemaMappingDefinition(final ExaMetadata exaMetadata,
            final AdapterProperties adapterProperties) {
        final JsonSchemaMappingReader mappingReader = createMappingReader(exaMetadata, adapterProperties);
        final List<EdmlInput> mappingDefinition = readMappingDefinition(adapterProperties);
        return mappingReader.readSchemaMapping(mappingDefinition);
    }

    private List<EdmlInput> readMappingDefinition(final AdapterProperties adapterProperties) {
        final DocumentAdapterProperties documentAdapterProperties = new DocumentAdapterProperties(adapterProperties);
        return documentAdapterProperties.getMappingDefinition();
    }

    private JsonSchemaMappingReader createMappingReader(final ExaMetadata exaMetadata,
            final AdapterProperties adapterProperties) {
        final ConnectionPropertiesReader connectionInformation = getConnectionInformation(exaMetadata,
                adapterProperties);
        final TableKeyFetcher tableKeyFetcher = this.dialect.getTableKeyFetcher(connectionInformation);
        final SchemaFetcher mappingFetcher = this.dialect.getSchemaFetcher(connectionInformation);
        return new JsonSchemaMappingReader(tableKeyFetcher, new SchemaInferencer(mappingFetcher));
    }

    private ConnectionPropertiesReader getConnectionInformation(final ExaMetadata exaMetadata,
            final AdapterProperties properties) {
        try {
            final String userGuideUrl = this.dialect.getUserGuideUrl();
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
     * Runs the actual query. The data is fetched using a scan (from DynamoDB or from document sources) and then
     * transformed into a {@code SELECT FROM VALUES} statement and passed back to Exasol.
     */
    @Override
    public final PushDownResponse pushdown(final ExaMetadata exaMetadata, final PushDownRequest request)
            throws AdapterException {
        final SqlStatement sqlQuery = request.getSelect();
        final String adapterNotes = request.getSchemaMetadataInfo().getAdapterNotes();
        // The adapter notes contain serialized table mappings (created from the edml definition when creating
        // the virtual schema)
        final RemoteTableQuery remoteTableQuery = new RemoteTableQueryFactory().build(sqlQuery, adapterNotes);
        final String responseStatement = runQuery(exaMetadata, request, remoteTableQuery);
        LOG.fine(() -> "Generated pushdown SQL: " + responseStatement);
        return PushDownResponse.builder()//
                .pushDownSql(responseStatement)//
                .build();
    }

    private String runQuery(final ExaMetadata exaMetadata, final PushDownRequest request,
            final RemoteTableQuery remoteTableQuery) {
        final AdapterProperties adapterProperties = getPropertiesFromRequest(request);
        final QueryPlanner queryPlanner = this.dialect
                .getQueryPlanner(getConnectionInformation(exaMetadata, adapterProperties), adapterProperties);
        final DocumentAdapterProperties documentAdapterProperties = new DocumentAdapterProperties(adapterProperties);
        final int availableClusterCores = new UdfCountCalculator().calculateMaxUdfInstanceCount(exaMetadata,
                documentAdapterProperties, this.thisNodesCoreCount);

        final QueryPlan queryPlan = queryPlanner.planQuery(remoteTableQuery, availableClusterCores);
        final String connectionName = getPropertiesFromRequest(request).getConnectionName();
        return new UdfCallBuilder(connectionName, exaMetadata.getScriptSchema(), this.dialect.getAdapterName())
                .getUdfCallSql(queryPlan, remoteTableQuery);
    }

    @Override
    public final RefreshResponse refresh(final ExaMetadata exaMetadata, final RefreshRequest refreshRequest) {
        final SchemaMetadata schemaMetadata = getSchemaMetadata(exaMetadata, getPropertiesFromRequest(refreshRequest));
        return RefreshResponse.builder().schemaMetadata(schemaMetadata).build();
    }

    @Override
    public final SetPropertiesResponse setProperties(final ExaMetadata exaMetadata,
            final SetPropertiesRequest request) {
        final Map<String, String> requestRawProperties = request.getProperties();
        final Map<String, String> mergedRawProperties = mergeProperties(request.getSchemaMetadataInfo().getProperties(),
                requestRawProperties);
        final AdapterProperties mergedProperties = new AdapterProperties(mergedRawProperties);
        final SchemaMetadata schemaMetadata = getSchemaMetadata(exaMetadata, mergedProperties);
        return SetPropertiesResponse.builder().schemaMetadata(schemaMetadata).build();
    }

    private Map<String, String> mergeProperties(final Map<String, String> previousRawProperties,
            final Map<String, String> requestRawProperties) {
        final Map<String, String> mergedRawProperties = new HashMap<>(previousRawProperties);
        for (final Map.Entry<String, String> requestRawProperty : requestRawProperties.entrySet()) {
            if (requestRawProperty.getValue() == null) {
                mergedRawProperties.remove(requestRawProperty.getKey());
            } else {
                mergedRawProperties.put(requestRawProperty.getKey(), requestRawProperty.getValue());
            }
        }
        LOG.info(() -> "Merged adapter properties:\n" //
                + "  " + formatMap("Previous", previousRawProperties) + "\n" //
                + "  " + formatMap("New", requestRawProperties) + "\n" //
                + "  " + formatMap("Merged", mergedRawProperties));
        return mergedRawProperties;
    }

    private String formatMap(final String name, final Map<String, String> map) {
        return name + " (" + map.size() + "): " + new TreeMap<>(map);
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
                            + "Supported {{capabilityType|uq}}-capabilities are [{{listOfSupported|uq}}].")
                    .parameter("capabilityType", capabilityType).parameter("listOfSupported", listOfSupported)
                    .toString());
        }
    }
}
