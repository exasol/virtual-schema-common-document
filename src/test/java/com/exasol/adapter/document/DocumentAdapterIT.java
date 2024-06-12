package com.exasol.adapter.document;

import static com.exasol.adapter.document.GenericUdfCallHandler.*;
import static com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT;
import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static com.exasol.matcher.TypeMatchMode.NO_JAVA_TYPE_CHECK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.edml.EdmlDefinition.EdmlDefinitionBuilder;
import com.exasol.adapter.document.edml.serializer.EdmlSerializer;
import com.exasol.adapter.document.mapping.reader.JsonSample;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.dbbuilder.dialects.exasol.udf.UdfScript;
import com.exasol.errorreporting.ExaError;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.testcontainers.ExasolTestcontainerTestSetup;
import com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter;
import com.exasol.udfdebugging.UdfTestSetup;

@Tag("integration")
@SuppressWarnings("try") // auto-closeable resource virtualSchema is never referenced in body of corresponding try
                         // statement
class DocumentAdapterIT {
    private static final String MY_VIRTUAL_SCHEMA = "MY_VIRTUAL_SCHEMA";
    private static final String ADAPTER_NAME = "FIXED_DATA_ADAPTER";
    private static final String MOCK_ADAPTER_JAR = "mock-adapter.jar";
    private static final Logger LOGGER = Logger.getLogger(DocumentAdapterIT.class.getSimpleName());
    private static final String VS_COMMON_DOCUMENT_VERSION_PROPERTY = "vs-common-document-version";
    private static final Pattern VS_COMMON_DOCUMENT_VERSION_PROPERTY_PATTERN = Pattern.compile(
            "<" + VS_COMMON_DOCUMENT_VERSION_PROPERTY + ">([^<]++)</" + VS_COMMON_DOCUMENT_VERSION_PROPERTY + ">");
    private static ExasolTestSetup testSetup;
    private static ExasolObjectFactory exasolObjectFactory;
    private static ConnectionDefinition nullConnection;
    private static AdapterScript adapterScript;
    private static Connection connection;
    private static UdfTestSetup udfTestSetup;

    @BeforeAll
    static void beforeAll() throws SQLException, BucketAccessException, TimeoutException, IOException {
        testSetup = ExasolTestcontainerTestSetup.start();
        connection = testSetup.createConnection();
        udfTestSetup = new UdfTestSetup(testSetup, connection);
        exasolObjectFactory = new ExasolObjectFactory(connection,
                ExasolObjectConfiguration.builder().withJvmOptions(udfTestSetup.getJvmOptions()).build());
        buildMockAdapter();
        final ExasolSchema adapterSchema = exasolObjectFactory.createSchema("ADAPTER");
        testSetup.getDefaultBucket().uploadFile(
                Path.of("test-project/mock-project/target").resolve(MOCK_ADAPTER_JAR).toAbsolutePath(),
                MOCK_ADAPTER_JAR);
        adapterScript = adapterSchema.createAdapterScriptBuilder("FILES_ADAPTER")
                .bucketFsContent("com.exasol.adapter.RequestDispatcher",
                        "/buckets/bfsdefault/default/" + MOCK_ADAPTER_JAR)
                .language(AdapterScript.Language.JAVA).build();
        nullConnection = exasolObjectFactory.createConnectionDefinition("NULL_CONNECTION", "", "", "{ }");
        createUdf(adapterSchema);
    }

    @AfterAll
    static void afterAll() {
        udfTestSetup.close();
        testSetup.close();
    }

    private static void createUdf(final ExasolSchema adapterSchema) {
        adapterSchema.createUdfBuilder("IMPORT_FROM_FIXED_DATA_ADAPTER").language(UdfScript.Language.JAVA)
                .inputType(UdfScript.InputType.SET).parameter(PARAMETER_DOCUMENT_FETCHER, "VARCHAR(2000000)")
                .parameter(PARAMETER_SCHEMA_MAPPING_REQUEST, "VARCHAR(2000000)")
                .parameter(PARAMETER_CONNECTION_NAME, "VARCHAR(500)").emits()
                .bucketFsContent("com.exasol.adapter.document.UdfEntryPoint",
                        "/buckets/bfsdefault/default/" + MOCK_ADAPTER_JAR)
                .build();
    }

    private static void buildMockAdapter() throws IOException {
        writeCurrentVersionToMockProjectPom();
        Verifier mvnRunner = null;
        try {
            final Path aggregatorProjectDir = Path.of("test-project/aggregator").toAbsolutePath();
            mvnRunner = new Verifier(aggregatorProjectDir.toString());
            final String java17JdkHome = getJava17JdkHome();
            LOGGER.info(() -> "Building mock-project at " + aggregatorProjectDir + " using JDK " + java17JdkHome);
            final Instant start = Instant.now();
            mvnRunner.setEnvironmentVariable("JAVA_HOME", java17JdkHome);
            mvnRunner.setSystemProperty("skipTests", "true");
            mvnRunner.setSystemProperty("maven.test.skip", "true");
            mvnRunner.setSystemProperty("ossindex.skip", "true");
            mvnRunner.setSystemProperty("maven.javadoc.skip", "true");
            mvnRunner.setSystemProperty("project-keeper.skip", "true");
            mvnRunner.addCliOption("-PalternateTargetDir");
            mvnRunner.addCliOption("--batch-mode");
            mvnRunner.executeGoal("package");
            mvnRunner.verifyErrorFreeLog();
            LOGGER.info("Done building mock-project in " + Duration.between(start, Instant.now()));
        } catch (final VerificationException exception) {
            if (mvnRunner != null) {
                mvnRunner.displayStreamBuffers();
            }
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-76")
                    .message("Failed to build mock-project. The project is used for the integration testing.")
                    .toString(), exception);
        }
    }

    /**
     * Maven build of aggregator module must run with Java 17. This is a workaround this project is migrated to Java 17.
     * <p>
     * This tries to find the path using environment variables {@code JAVA17_HOME} or {@code JAVA_HOME_17_X64}.
     * 
     * @return path to JDK 17 home.
     */
    private static String getJava17JdkHome() {
        final List<String> envVariables = List.of("JAVA17_HOME", "JAVA_HOME_17_X64");
        return findEnvVariable(envVariables) //
                .or(() -> currentJvm()) //
                .orElseThrow(
                        () -> new IllegalStateException("Failed to detect JDK 17 using env variables " + envVariables));
    }

    private static Optional<String> findEnvVariable(final List<String> envVariables) {
        return envVariables.stream().map(System::getenv).filter(value -> value != null).findFirst();
    }

    private static Optional<String> currentJvm() {
        if (Runtime.version().feature() == 17) {
            return Optional.of(System.getProperty("java.home"));
        } else {
            return Optional.empty();
        }
    }

    private static void writeCurrentVersionToMockProjectPom() throws IOException {
        final Path mockProjectPom = Path.of("test-project/mock-project/pom.xml");
        final String mockProjectPomContent = Files.readString(mockProjectPom);
        final String vsCommonDocumentVersion = MavenProjectVersionGetter.getCurrentProjectVersion();
        final String updatedPom = VS_COMMON_DOCUMENT_VERSION_PROPERTY_PATTERN.matcher(mockProjectPomContent)
                .replaceAll("<" + VS_COMMON_DOCUMENT_VERSION_PROPERTY + ">" + vsCommonDocumentVersion + "</"
                        + VS_COMMON_DOCUMENT_VERSION_PROPERTY + ">");
        Files.writeString(mockProjectPom, updatedPom);
    }

    @Test
    void testUseMappingFromFile() throws Exception {
        final String content = JsonSample.builder().basic().withFields(JsonSample.ADDITIONAL_FIELDS).build();
        testSetup.getDefaultBucket().uploadStringContent(content, "mapping.json");
        try (final VirtualSchema virtualSchema = createVirtualSchema("/bfsdefault/default/mapping.json")) {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement
                    .executeQuery("SELECT ISBN, NAME FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;");
            assertThat(resultSet,
                    table("VARCHAR", "VARCHAR").row("123456789", "Tom Sawyer").matches(NO_JAVA_TYPE_CHECK));
        }
    }

    @Test
    void testToDoubleMapping() {
        final Fields mapping = Fields.builder()//
                .mapField("isbn", ToDoubleMapping.builder().notNumericBehaviour(CONVERT_OR_ABORT).build())//
                .build();
        final String query = "SELECT ISBN FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;";
        final Matcher<ResultSet> expectedResult = table("DOUBLE PRECISION").row("123456789")
                .matches(NO_JAVA_TYPE_CHECK);
        assertVirtualSchemaQuery(mapping, query, expectedResult);
    }

    @Test
    void testToBoolMapping() {
        final Fields mapping = Fields.builder()//
                .mapField("name", ToBoolMapping.builder().notBooleanBehavior(CONVERT_OR_ABORT).build())//
                .build();
        final String query = "SELECT NAME FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;";
        final Matcher<ResultSet> expectedResult = table("BOOLEAN").row(false).matches(NO_JAVA_TYPE_CHECK);
        assertVirtualSchemaQuery(mapping, query, expectedResult);
    }

    @Test
    void testLikeWithIllegalEscapeChar() throws SQLException {
        final Fields mapping = Fields.builder()//
                .mapField("name", ToBoolMapping.builder().notBooleanBehavior(CONVERT_OR_ABORT).build())//
                .build();
        final String query = "SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS WHERE NAME LIKE 'test' ESCAPE ':';";
        assertVirtualSchemaQueryFails(mapping, query, containsString(
                "E-VSD-99: This virtual-schema only supports LIKE predicates with '\\' as escape character. Please add ESCAPE '\\'."));
    }

    @Test
    void testToDateMapping() {
        final Fields mapping = Fields.builder()//
                .mapField("publication_date", ToDateMapping.builder().notDateBehavior(CONVERT_OR_ABORT).build())//
                .build();
        final String query = "SELECT PUBLICATION_DATE FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;";
        final Matcher<ResultSet> expectedResult = table("DATE").row(new Date(1632297287000L))//
                .withUtcCalendar()//
                .matches(NO_JAVA_TYPE_CHECK);
        assertVirtualSchemaQuery(mapping, query, expectedResult);
    }

    @Test
    void testToTimestampMapping() {
        final Fields mapping = Fields.builder()//
                .mapField("my_timestamp", ToTimestampMapping.builder().notTimestampBehavior(CONVERT_OR_ABORT).build())//
                .build();
        final String query = "SELECT MY_TIMESTAMP FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;";
        final Matcher<ResultSet> expectedResult = table("TIMESTAMP").row(new Timestamp(1632297287000L))
                .withUtcCalendar()//
                .matches(NO_JAVA_TYPE_CHECK);
        assertVirtualSchemaQuery(mapping, query, expectedResult);
    }

    @ParameterizedTest
    @MethodSource("emptyQueryPlanTypes")
    void testEmptyQueryPlan(final Fields mapping, final String expectedColumnType) {
        assertThat(mapping.getFieldsMap().size(), equalTo(1));
        final String fieldName = mapping.getFieldsMap().keySet().iterator().next();
        final String query = "SELECT " + fieldName + " FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS";
        final Matcher<ResultSet> expectedResult = table(expectedColumnType).matches();
        assertVirtualSchemaQueryWithEmptyQueryPlan(mapping, query, expectedResult);
    }

    static Stream<Arguments> emptyQueryPlanTypes() {
        return Stream.of(fieldType("my_timestamp", ToTimestampMapping.builder().build(), "TIMESTAMP"), //
                fieldType("publication_date", ToDateMapping.builder().build(), "DATE"),
                fieldType("isbn", ToDoubleMapping.builder().build(), "DOUBLE PRECISION"),
                fieldType("name", ToBoolMapping.builder().build(), "BOOLEAN"),
                fieldType("name", ToVarcharMapping.builder().build(), "VARCHAR"),
                fieldType("isbn", ToDecimalMapping.builder().build(), "BIGINT"));
    }

    static Arguments fieldType(final String fieldName, final MappingDefinition mapping,
            final String expectedColumnType) {
        return Arguments.of(Fields.builder().mapField(fieldName, mapping).build(), expectedColumnType);
    }

    private void assertVirtualSchemaQueryFails(final Fields mapping, final String query,
            final Matcher<String> exceptionMessageMatcher) throws SQLException {
        try (final VirtualSchema virtualSchema = createVirtualSchema("", mapping);
                final Statement statement = connection.createStatement()) {
            final SQLException exception = assertThrows(SQLException.class, () -> statement.executeQuery(query));
            assertThat(exception.getMessage(), exceptionMessageMatcher);
        }
    }

    private void assertVirtualSchemaQuery(final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult) {
        assertVirtualSchemaQuery("", mapping, query, expectedResult);
    }

    private void assertVirtualSchemaQueryWithEmptyQueryPlan(final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult) {
        assertVirtualSchemaQuery("EmptyQueryPlan", mapping, query, expectedResult);
    }

    private void assertVirtualSchemaQuery(final String source, final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult) {
        try (VirtualSchema virtualSchema = createVirtualSchema(source, mapping)) {
            assertQueryResult(query, expectedResult);
        }
    }

    private VirtualSchema createVirtualSchema(final String source, final MappingDefinition mapping) {
        final EdmlDefinition edml = EdmlDefinition.builder().source(source)//
                .destinationTable("BOOKS")//
                .mapping(mapping).build();
        final String edmlString = new EdmlSerializer().serialize(edml);
        return createVirtualSchema(edmlString);
    }

    private VirtualSchema createVirtualSchema(final String mappingProperty) {
        return exasolObjectFactory.createVirtualSchemaBuilder(MY_VIRTUAL_SCHEMA).connectionDefinition(nullConnection)
                .adapterScript(adapterScript).dialectName(ADAPTER_NAME).properties(Map.of("MAPPING", mappingProperty))
                .build();
    }

    @Test
    void testInlineMappingArray() {
        final Fields mapping = Fields.builder()//
                .mapField("isbn", ToDoubleMapping.builder().notNumericBehaviour(CONVERT_OR_ABORT).build())//
                .build();
        final EdmlDefinition t1 = EdmlDefinition.builder().source("")//
                .destinationTable("T1")//
                .mapping(mapping).build();
        final EdmlDefinition t2 = EdmlDefinition.builder().source("")//
                .destinationTable("T2")//
                .mapping(mapping).build();
        final EdmlSerializer edmlSerializer = new EdmlSerializer();
        final String mappingString = "[" + edmlSerializer.serialize(t1) + ", " + edmlSerializer.serialize(t2) + "]";
        try (final VirtualSchema virtualSchema = createVirtualSchema(mappingString)) {
            assertQueryResult(
                    "SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".T1 UNION ALL SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".T2",
                    table().row("123456789").row("123456789").matches(NO_JAVA_TYPE_CHECK));
        }
    }

    @Test
    void testSetProperty() throws SQLException {
        final EdmlDefinitionBuilder builder = EdmlDefinition.builder().source("")//
                .destinationTable("BOOKS");
        final EdmlDefinition mapping1 = builder.mapping(Fields.builder()//
                .mapField("isbn", ToVarcharMapping.builder().build())//
                .build()).build();
        final EdmlDefinition mapping2 = builder.mapping(Fields.builder()//
                .mapField("isbn", ToVarcharMapping.builder().build())//
                .mapField("publication_date", ToDateMapping.builder().notDateBehavior(CONVERT_OR_ABORT).build())//
                .build()).build();
        final EdmlSerializer edmlSerializer = new EdmlSerializer();

        try (final VirtualSchema virtualSchema = createVirtualSchema(edmlSerializer.serialize(mapping1));
                final Statement statement = connection.createStatement()) {
            assertQueryResult("SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS",
                    table("VARCHAR").row("123456789").matches());

            statement.execute("ALTER VIRTUAL SCHEMA " + MY_VIRTUAL_SCHEMA + " SET MAPPING = '"
                    + edmlSerializer.serialize(mapping2) + "'");

            assertQueryResult("SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS",
                    table("VARCHAR", "DATE").row("123456789", Date.valueOf("2021-09-22")).matches());
        }
    }

    @Test
    void testRefresh() throws SQLException {
        final EdmlDefinition mapping1 = EdmlDefinition.builder().source("")//
                .destinationTable("BOOKS")//
                .mapping(Fields.builder()//
                        .mapField("isbn", ToVarcharMapping.builder().build())//
                        .build())
                .build();
        final EdmlSerializer edmlSerializer = new EdmlSerializer();
        try (final VirtualSchema virtualSchema = createVirtualSchema(edmlSerializer.serialize(mapping1));
                final Statement statement = connection.createStatement()) {
            assertQueryResult("SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS",
                    table("VARCHAR").row("123456789").matches());

            statement.execute("ALTER VIRTUAL SCHEMA " + MY_VIRTUAL_SCHEMA + " REFRESH");

            assertQueryResult("SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS",
                    table("VARCHAR").row("123456789").matches());
        }
    }

    private void assertQueryResult(final String sql, final Matcher<ResultSet> matcher) {
        try (final Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery(sql)) {
            assertThat(resultSet, matcher);
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to run query '" + sql + "': " + exception.getMessage());
        }
    }
}
