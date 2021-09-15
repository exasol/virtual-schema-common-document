package com.exasol.adapter.document;

import static com.exasol.adapter.document.UdfEntryPoint.*;
import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static com.exasol.matcher.TypeMatchMode.NO_JAVA_TYPE_CHECK;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.jupiter.api.*;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.dbbuilder.dialects.exasol.udf.UdfScript;
import com.exasol.errorreporting.ExaError;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.testcontainers.ExasolTestcontainerTestSetup;
import com.exasol.udfdebugging.UdfTestSetup;

@Tag("integration")
class DocumentAdapterIT {
    private static final String MY_VIRTUAL_SCHEMA = "MY_VIRTUAL_SCHEMA";
    private static final String ADAPTER_NAME = "FIXED_DATA_ADAPTER";
    private static final String MOCK_ADAPTER_JAR = "mock-adapter.jar";
    private static final Logger LOGGER = Logger.getLogger(DocumentAdapterIT.class.getSimpleName());
    private static ExasolTestSetup testSetup;
    private static ExasolObjectFactory exasolObjectFactory;
    private static ConnectionDefinition nullConnection;
    private static AdapterScript adapterScript;

    @BeforeAll
    static void beforeAll() throws SQLException, BucketAccessException, TimeoutException, FileNotFoundException {
        testSetup = new ExasolTestcontainerTestSetup();
        final UdfTestSetup udfTestSetup = new UdfTestSetup(testSetup);
        exasolObjectFactory = new ExasolObjectFactory(testSetup.createConnection(),
                ExasolObjectConfiguration.builder().withJvmOptions(udfTestSetup.getJvmOptions()).build());
        buildMockAdapter();
        final ExasolSchema adapterSchema = exasolObjectFactory.createSchema("ADAPTER");
        testSetup.getDefaultBucket().uploadFile(Path.of("test-project/mock-project/target", MOCK_ADAPTER_JAR),
                MOCK_ADAPTER_JAR);
        adapterScript = adapterSchema.createAdapterScriptBuilder("FILES_ADAPTER")
                .bucketFsContent("com.exasol.adapter.RequestDispatcher",
                        "/buckets/bfsdefault/default/" + MOCK_ADAPTER_JAR)
                .language(AdapterScript.Language.JAVA).build();
        nullConnection = exasolObjectFactory.createConnectionDefinition("NULL_CONNECTION", "nowhere", "user", "pass");
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
                .bucketFsContent(UdfEntryPoint.class.getName(), "/buckets/bfsdefault/default/" + MOCK_ADAPTER_JAR)
                .build();
    }

    private static void buildMockAdapter() {
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

    @Test
    void testUseMappingFromFile() throws BucketAccessException, TimeoutException, SQLException {
        testSetup.getDefaultBucket().uploadInputStream(
                () -> getClass().getClassLoader().getResourceAsStream("basicMapping.json"), "mapping.json");
        final VirtualSchema virtualSchema = createVirtualSchema("/bfsdefault/default/mapping.json");
        try {
            final Statement statement = testSetup.createConnection().createStatement();
            final ResultSet resultSet = statement
                    .executeQuery("SELECT ISBN, NAME FROM " + MY_VIRTUAL_SCHEMA + ".BOOKS;");
            assertThat(resultSet,
                    table("VARCHAR", "VARCHAR").row("123456789", "Tom Sawyer").matches(NO_JAVA_TYPE_CHECK));
        } finally {
            virtualSchema.drop();
        }
    }

    private VirtualSchema createVirtualSchema(final String mappingProperty) {
        return exasolObjectFactory.createVirtualSchemaBuilder(MY_VIRTUAL_SCHEMA).connectionDefinition(nullConnection)
                .adapterScript(adapterScript).dialectName(ADAPTER_NAME).properties(Map.of("MAPPING", mappingProperty))
                .build();
    }
}
