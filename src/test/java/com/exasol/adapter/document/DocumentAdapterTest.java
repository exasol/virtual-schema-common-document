package com.exasol.adapter.document;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.*;
import com.exasol.adapter.AdapterException;
import com.exasol.adapter.capabilities.Capabilities;
import com.exasol.adapter.capabilities.MainCapability;
import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.edml.serializer.EdmlSerializer;
import com.exasol.adapter.metadata.SchemaMetadata;
import com.exasol.adapter.metadata.SchemaMetadataInfo;
import com.exasol.adapter.request.RefreshRequest;
import com.exasol.adapter.request.SetPropertiesRequest;
import com.exasol.adapter.response.GetCapabilitiesResponse;

@ExtendWith(MockitoExtension.class)
class DocumentAdapterTest {
    private static final String CONNECTION_NAME = "connection_name";
    final EdmlSerializer edmlSerializer = new EdmlSerializer();
    @Mock
    DocumentAdapterDialect dialectMock;
    @Mock
    ExaMetadata exaMetadataMock;
    @Mock
    ExaConnectionInformation exaConnectionInfoMock;

    @Test
    void testUnsupportedMainCapability() {
        when(dialectMock.getCapabilities())
                .thenReturn(Capabilities.builder().addMain(MainCapability.AGGREGATE_HAVING).build());
        final DocumentAdapter documentAdapter = testee();
        final UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> documentAdapter.getCapabilities(null, null));
        assertThat(exception.getMessage(), matchesPattern(Pattern.quote(
                "F-VSD-3: This dialect specified main-capabilities ('AGGREGATE_HAVING') that are not supported by the abstract DocumentAdapter. Please remove the capability from the specific adapter implementation. Supported main-capabilities are [")
                + "[^]]*\\]\\."));
    }

    @Test
    void testSupportedCapabilities() throws AdapterException {
        final Capabilities capabilities = Capabilities.builder().addMain(MainCapability.SELECTLIST_PROJECTION).build();
        when(dialectMock.getCapabilities()).thenReturn(capabilities);
        final GetCapabilitiesResponse response = testee().getCapabilities(null, null);
        assertThat(response.getCapabilities(), equalTo(capabilities));
    }

    @Test
    void testSetPropertiesNoChanges() throws ExaConnectionAccessException {
        final EdmlDefinition mapping1 = EdmlDefinition.builder().source("").destinationTable("BOOKS")//
                .mapping(Fields.builder().mapField("isbn", ToVarcharMapping.builder().build()).build()).build();

        final SchemaMetadata response = callSetProperties(
                Map.of("CONNECTION_NAME", CONNECTION_NAME, "MAPPING", edmlSerializer.serialize(mapping1)), emptyMap());
        assertSchema(response, "BOOKS (ISBN VARCHAR(254) UTF8)");
    }

    @Test
    void testSetPropertiesMappingUpdated() throws ExaConnectionAccessException {
        final EdmlDefinition mapping1 = EdmlDefinition.builder().source("").destinationTable("BOOKS")//
                .mapping(Fields.builder().mapField("isbn", ToVarcharMapping.builder().build()).build()).build();
        final EdmlDefinition mapping2 = EdmlDefinition.builder().source("").destinationTable("BOOKS")//
                .mapping(Fields.builder().mapField("release_date", ToDateMapping.builder().build()).build()).build();

        final SchemaMetadata response = callSetProperties(
                Map.of("CONNECTION_NAME", CONNECTION_NAME, "MAPPING", edmlSerializer.serialize(mapping1)),
                Map.of("MAPPING", edmlSerializer.serialize(mapping2)));
       assertSchema(response, "BOOKS (RELEASE_DATE DATE)");
    }

    private void assertSchema(final SchemaMetadata schemaMetadata, final String expectedTables) {
         assertAll(() -> assertThat(schemaMetadata.getAdapterNotes(), not(emptyString())),
                () -> assertThat(schemaMetadata.getTables(), hasSize(1)),
                () -> assertThat(schemaMetadata.getTables().get(0).describe(), equalTo(expectedTables)));
    }

    @Test
    void testSetPropertiesLogLevelAdded() throws ExaConnectionAccessException {
        final EdmlDefinition mapping1 = EdmlDefinition.builder().source("").destinationTable("BOOKS")//
                .mapping(Fields.builder().mapField("isbn", ToVarcharMapping.builder().build()).build()).build();

        final SchemaMetadata response = callSetProperties(
                Map.of("CONNECTION_NAME", CONNECTION_NAME, "MAPPING", edmlSerializer.serialize(mapping1)),
                Map.of("LOG_LEVEL", "DEBUG"));
        assertSchema(response, "BOOKS (ISBN VARCHAR(254) UTF8)");
    }

    @Test
    void testSetPropertiesRemoveProperty() throws ExaConnectionAccessException {
        final EdmlDefinition mapping1 = EdmlDefinition.builder().source("").destinationTable("BOOKS")//
                .mapping(Fields.builder().mapField("isbn", ToVarcharMapping.builder().build()).build()).build();

        final Map<String, String> newProperties = new HashMap<>();
        newProperties.put("MAPPING", null);
        final Map<String, String> previousProperties = Map.of("CONNECTION_NAME", CONNECTION_NAME, "MAPPING",
                edmlSerializer.serialize(mapping1));
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> callSetProperties(previousProperties, newProperties));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-72: Missing mandatory MAPPING property. Please set MAPPING to the path to your schema mapping files in the BucketFS."));
    }

    private SchemaMetadata callSetProperties(final Map<String, String> previousProperties, final Map<String, String> newProperties)
            throws ExaConnectionAccessException {
        when(exaMetadataMock.getConnection(CONNECTION_NAME)).thenReturn(exaConnectionInfoMock);
        when(exaConnectionInfoMock.getPassword()).thenReturn("{}");
        final SchemaMetadataInfo schemaMetadataInfo = new SchemaMetadataInfo("adapterName", "notes", previousProperties);
        final SetPropertiesRequest request = new SetPropertiesRequest(schemaMetadataInfo, newProperties);
        return testee().setProperties(exaMetadataMock, request)
            .getSchemaMetadata();
    }

    @Test
    void testRefresh() throws ExaConnectionAccessException {
        final EdmlDefinition mapping1 = EdmlDefinition.builder().source("").destinationTable("BOOKS")//
                .mapping(Fields.builder().mapField("isbn", ToVarcharMapping.builder().build()).build()).build();

        final SchemaMetadata response = callRefresh(Map.of("CONNECTION_NAME", CONNECTION_NAME,
                                    "MAPPING", edmlSerializer.serialize(mapping1)));
        assertSchema(response, "BOOKS (ISBN VARCHAR(254) UTF8)");
    }

    private SchemaMetadata callRefresh(final Map<String, String> previousProperties)
            throws ExaConnectionAccessException {
        when(exaMetadataMock.getConnection(CONNECTION_NAME)).thenReturn(exaConnectionInfoMock);
        when(exaConnectionInfoMock.getPassword()).thenReturn("{}");
        final SchemaMetadataInfo schemaMetadataInfo = new SchemaMetadataInfo("adapterName", "notes", previousProperties);
        final RefreshRequest request = new RefreshRequest(schemaMetadataInfo);
        return testee().refresh(exaMetadataMock, request).getSchemaMetadata();
    }

    private DocumentAdapter testee() {
        return new DocumentAdapter(dialectMock);
    }
}
