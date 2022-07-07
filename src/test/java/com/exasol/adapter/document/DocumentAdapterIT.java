package com.exasol.adapter.document;

import static com.exasol.adapter.document.GenericUdfCallHandler.*;
import static com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT;
import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static com.exasol.matcher.TypeMatchMode.NO_JAVA_TYPE_CHECK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.edml.serializer.EdmlSerializer;
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

    @BeforeAll
    static void beforeAll() throws SQLException, BucketAccessException, TimeoutException, IOException {
        testSetup = new ExasolTestcontainerTestSetup();
        connection = testSetup.createConnection();
        final UdfTestSetup udfTestSetup = new UdfTestSetup(testSetup, connection);
        exasolObjectFactory = new ExasolObjectFactory(connection,
                ExasolObjectConfiguration.builder().withJvmOptions(udfTestSetup.getJvmOptions()).build());
        buildMockAdapter();
        final ExasolSchema adapterSchema = exasolObjectFactory.createSchema("ADAPTER");
        testSetup.getDefaultBucket().uploadFile(Path.of("test-project/mock-project/target", MOCK_ADAPTER_JAR),
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
        try {
            LOGGER.info("Building mock-project");
            final Verifier mvnRunner = new Verifier(Path.of("test-project", "aggregator").toAbsolutePath().toString());
            mvnRunner.setSystemProperty("skipTests", "true");
            mvnRunner.setSystemProperty("maven.test.skip", "true");
            mvnRunner.setSystemProperty("ossindex.skip", "true");
            mvnRunner.setSystemProperty("maven.javadoc.skip", "true");
            mvnRunner.setSystemProperty("lombok.delombok.skip", "true");
            mvnRunner.setSystemProperty("project-keeper.skip", "true");
            mvnRunner.addCliOption("-PalternateTargetDir");
            mvnRunner.executeGoal("package");
            LOGGER.info("Done building mock-project");
        } catch (final VerificationException exception) {
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
    void testUseMappingFromFile() throws BucketAccessException, TimeoutException, SQLException {
        testSetup.getDefaultBucket().uploadInputStream(
                () -> getClass().getClassLoader().getResourceAsStream("basicMapping.json"), "mapping.json");
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
    void testLikeWithIllegalEscapeChar() {
        final Fields mapping = Fields.builder()//
                .mapField("name", ToBoolMapping.builder().notBooleanBehavior(CONVERT_OR_ABORT).build())//
                .build();
        final String query = "SELECT * FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS WHERE NAME LIKE 'test' ESCAPE ':';";
        final SQLException exception = assertThrows(SQLException.class,
                () -> assertVirtualSchemaQuery(mapping, query, null));
        assertThat(exception.getMessage(), containsString(
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
                .mapField("my_timestamp",
                        ToTimestampMapping.builder().notTimestampBehavior(CONVERT_OR_ABORT)
                                .useTimestampWithLocalTimezoneType(false).build())//
                .build();
        final String query = "SELECT MY_TIMESTAMP FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;";
        final Matcher<ResultSet> expectedResult = table("TIMESTAMP").row(new Timestamp(1632297287000L))
                .withUtcCalendar()//
                .matches(NO_JAVA_TYPE_CHECK);
        assertVirtualSchemaQuery(mapping, query, expectedResult);
    }

    @ParameterizedTest
    @ValueSource(strings = { "UTC", "EUROPE/BERLIN" })
    void testToTimestampMappingWithLocalTimezone(final String sessionTimezone) throws SQLException {
        final Fields mapping = Fields.builder()//
                .mapField("my_timestamp",
                        ToTimestampMapping.builder().notTimestampBehavior(CONVERT_OR_ABORT)
                                .useTimestampWithLocalTimezoneType(true).build())//
                .build();
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER SESSION SET TIME_ZONE = '" + sessionTimezone + "';");
            final String query = "SELECT MY_TIMESTAMP FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;";
            final Matcher<ResultSet> expectedResult = table("TIMESTAMP").row(new Timestamp(1632297287000L))
                    .withCalendar(Calendar.getInstance(TimeZone.getTimeZone(sessionTimezone)))//
                    .matches(NO_JAVA_TYPE_CHECK);
            assertVirtualSchemaQuery(mapping, query, expectedResult, statement);
        }
    }

    private void assertVirtualSchemaQuery(final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult) throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            assertVirtualSchemaQuery(mapping, query, expectedResult, statement);
        }
    }

    private void assertVirtualSchemaQuery(final MappingDefinition mapping, final String query,
            final Matcher<ResultSet> expectedResult, final Statement statement) throws SQLException {
        final VirtualSchema virtualSchema = createVirtualSchema(mapping);
        try {
            final ResultSet resultSet = statement.executeQuery(query);
            assertThat(resultSet, expectedResult);
        } finally {
            virtualSchema.drop();
        }
    }

    private VirtualSchema createVirtualSchema(final MappingDefinition mapping) {
        final EdmlDefinition edml = EdmlDefinition.builder().source("")//
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
