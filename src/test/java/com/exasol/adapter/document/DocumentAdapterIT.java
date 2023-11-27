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
import java.util.Map;
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
        testSetup = new ExasolTestcontainerTestSetup();
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
    static void afterAll() throws Exception {
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
            mvnRunner = new Verifier(Path.of("test-project/aggregator").toAbsolutePath().toString());
            LOGGER.info(() -> "Building mock-project at " + Path.of("test-project/aggregator").toAbsolutePath());
            mvnRunner.setSystemProperty("skipTests", "true");
            mvnRunner.setSystemProperty("maven.test.skip", "true");
            mvnRunner.setSystemProperty("ossindex.skip", "true");
            mvnRunner.setSystemProperty("maven.javadoc.skip", "true");
            mvnRunner.setSystemProperty("lombok.delombok.skip", "true");
            mvnRunner.setSystemProperty("project-keeper.skip", "true");
            mvnRunner.addCliOption("-PalternateTargetDir");
            mvnRunner.executeGoal("package");
            mvnRunner.verifyErrorFreeLog();
            LOGGER.info("Done building mock-project");
        } catch (final VerificationException exception) {
            if (mvnRunner != null) {
                mvnRunner.displayStreamBuffers();
            }
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-76")
                    .message("Failed to build mock-project. The project is used for the integration testing.")
                    .toString(), exception);
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
        final VirtualSchema virtualSchema = createVirtualSchema("/bfsdefault/default/mapping.json");
        try {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement
                    .executeQuery("SELECT ISBN, NAME FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;");
            assertThat(resultSet,
                    table("VARCHAR", "VARCHAR").row("123456789", "Tom Sawyer").matches(NO_JAVA_TYPE_CHECK));
        } finally {
            virtualSchema.drop();
        }
    }

    @Test
    void testToDoubleMapping() throws SQLException {
        final Fields mapping = Fields.builder()//
                .mapField("isbn", ToDoubleMapping.builder().notNumericBehaviour(CONVERT_OR_ABORT).build())//
                .build();
        final String query = "SELECT ISBN FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;";
        final Matcher<ResultSet> expectedResult = table("DOUBLE PRECISION").row("123456789")
                .matches(NO_JAVA_TYPE_CHECK);
        assertVirtualSchemaQuery(mapping, query, expectedResult);
    }

    @Test
    void testToBoolMapping() throws SQLException {
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
    void testToDateMapping() throws SQLException {
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
    void testToTimestampMapping() throws SQLException {
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
    void testEmptyQueryPlan(final Fields mapping, final String expectedColumnType) throws SQLException {
        assertThat(mapping.getFieldsMap().size(), equalTo(1));
        final String fieldName = mapping.getFieldsMap().keySet().iterator().next();
        try (final Statement statement = connection.createStatement()) {
            final String query = "SELECT " + fieldName + " FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS";
            final Matcher<ResultSet> expectedResult = table(expectedColumnType).matches();
            assertVirtualSchemaQueryWithEmptyQueryPlan(mapping, query, expectedResult, statement);
        }
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
        final VirtualSchema virtualSchema = createVirtualSchema("", mapping);
        try (final Statement statement = connection.createStatement()) {
            final SQLException exception = assertThrows(SQLException.class, () -> statement.executeQuery(query));
            assertThat(exception.getMessage(), exceptionMessageMatcher);
        } finally {
            virtualSchema.drop();
        }
    }

    private void assertVirtualSchemaQuery(final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult) throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            assertVirtualSchemaQuery(mapping, query, expectedResult, statement);
        }
    }

    private void assertVirtualSchemaQuery(final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult, final Statement statement) {
        assertVirtualSchemaQuery("", mapping, query, expectedResult, statement);
    }

    private void assertVirtualSchemaQueryWithEmptyQueryPlan(final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult, final Statement statement) {
        assertVirtualSchemaQuery("EmptyQueryPlan", mapping, query, expectedResult, statement);
    }

    private void assertVirtualSchemaQuery(final String source, final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult, final Statement statement) {
        final VirtualSchema virtualSchema = createVirtualSchema(source, mapping);
        try (final ResultSet resultSet = statement.executeQuery(query)) {
            assertThat(resultSet, expectedResult);
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to execute query '" + query + "': " + exception.getMessage(),
                    exception);
        } finally {
            virtualSchema.drop();
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
    void testInlineMappingArray() throws SQLException {
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
        final VirtualSchema virtualSchema = createVirtualSchema(mappingString);
        try (final Statement statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".T1 UNION ALL SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".T2");
            assertThat(resultSet, table().row("123456789").row("123456789").matches(NO_JAVA_TYPE_CHECK));
        } finally {
            virtualSchema.drop();
        }
    }
}
