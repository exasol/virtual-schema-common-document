package com.exasol.adapter.document;

import static com.exasol.utils.LogHelper.logFine;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.ExaConnectionAccessException;
import com.exasol.ExaConnectionInformation;
import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.VirtualSchemaAdapter;
import com.exasol.adapter.capabilities.*;
import com.exasol.adapter.document.connection.ConnectionPropertiesReader;
import com.exasol.adapter.document.connection.ConnectionStringReader;
import com.exasol.adapter.document.mapping.SchemaMapping;
import com.exasol.adapter.document.mapping.SchemaMappingToSchemaMetadataConverter;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
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

    private final Logger logger;

    /**
     * Create a new instance of {@link DocumentAdapter}.
     *
     * @param dialect dialect implementation
     */
    public DocumentAdapter(final DocumentAdapterDialect dialect) {
        this(dialect, Logger.getLogger(DocumentAdapter.class.getName()));
    }

    /**
     * Create a new instance of {@link DocumentAdapter} with a custom logger.
     * <p>
     * This constructor is primarily intended for testing or advanced logging configuration.
     * </p>
     *
     * @param dialect dialect implementation
     * @param logger  the logger to use for internal logging
     */
    public DocumentAdapter(final DocumentAdapterDialect dialect, final Logger logger) {
        this.dialect = dialect;
        this.thisNodesCoreCount = Runtime.getRuntime().availableProcessors();
        this.logger = logger;
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

    AdapterProperties getPropertiesFromRequest(final AdapterRequest request) {
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
        logger.fine(() -> "Generated pushdown SQL: " + responseStatement);
        return PushDownResponse.builder()//
                .pushDownSql(responseStatement)//
                .build();
    }

    /**
     * Plans a query for a remote table and generates the corresponding SQL string
     * that calls a UDF (User-Defined Function) to execute the query.
     * <p>
     * This method follows these steps:
     * <ul>
     *     <li>Logs the beginning of the planning process for the given remote table query.</li>
     *     <li>Extracts adapter and connection properties from the request.</li>
     *     <li>Obtains a {@link QueryPlanner} from the dialect using the connection information.</li>
     *     <li>Calculates the number of cluster cores available for parallel execution.</li>
     *     <li>Builds a {@link QueryPlan} for the remote table query.</li>
     *     <li>Logs a summary of the generated plan and execution configuration.</li>
     *     <li>Generates the SQL statement that calls the UDF to execute the planned query.</li>
     *     <li>Logs the generated UDF call.</li>
     * </ul>
     *
     * @param exaMetadata       metadata provided by the Exasol UDF framework
     * @param request           the pushdown request containing the query and adapter context
     * @param remoteTableQuery  the query that targets the remote table
     * @return SQL string that calls the UDF to execute the planned query
     */
    private String runQuery(final ExaMetadata exaMetadata, final PushDownRequest request,
                            final RemoteTableQuery remoteTableQuery) {
        logFine(logger, "Starting to plan query | Remote table query: %s", remoteTableQuery.toString());

        final AdapterProperties adapterProperties = getPropertiesFromRequest(request);
        final QueryPlanner queryPlanner = this.dialect
                .getQueryPlanner(getConnectionInformation(exaMetadata, adapterProperties), adapterProperties);

        final DocumentAdapterProperties documentAdapterProperties = new DocumentAdapterProperties(adapterProperties);
        final int availableClusterCores = new UdfCountCalculator().calculateMaxUdfInstanceCount(
                exaMetadata, documentAdapterProperties, this.thisNodesCoreCount);

        final QueryPlan queryPlan = queryPlanner.planQuery(remoteTableQuery, availableClusterCores);

        final String connectionName = adapterProperties.getConnectionName();
        final String scriptSchema = exaMetadata.getScriptSchema();

        logFine(logger, "Planned query with %d cluster cores | Script schema: '%s' | Plan type: '%s' | Adapter: '%s' | Connection: '%s'",
                availableClusterCores,
                scriptSchema,
                queryPlan.getClass().getSimpleName(),
                this.dialect.getAdapterName(),
                connectionName
        );

        final String udfCall = new UdfCallBuilder(connectionName, scriptSchema, this.dialect.getAdapterName())
                .getUdfCallSql(queryPlan, remoteTableQuery);

        logFine(logger, "Generated UDF call: %s | Remote table query: %s", udfCall, remoteTableQuery.toString());

        return udfCall;
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
        logger.info(() -> "Merged adapter properties:\n" //
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
