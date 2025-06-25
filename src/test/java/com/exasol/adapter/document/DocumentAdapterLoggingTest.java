package com.exasol.adapter.document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.exasol.ExaConnectionInformation;
import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.document.queryplan.EmptyQueryPlan;
import com.exasol.adapter.document.queryplan.QueryPlan;
import com.exasol.adapter.document.queryplanning.RemoteTableQuery;
import com.exasol.adapter.request.AdapterRequest;
import com.exasol.adapter.request.PushDownRequest;

class DocumentAdapterLoggingTest {

    private Logger mockLogger;
    private DocumentAdapter adapter;
    private DocumentAdapterDialect dialectMock;

    private AdapterProperties mockedAdapterProperties;

    @BeforeEach
    void setUp() {
        mockLogger = mock(Logger.class);
        when(mockLogger.isLoggable(Level.FINE)).thenReturn(true);

        dialectMock = mock(DocumentAdapterDialect.class);
        // Fix: return non-null user guide URL to avoid IllegalArgumentException
        when(dialectMock.getUserGuideUrl()).thenReturn("https://example.com/user-guide");

        mockedAdapterProperties = mock(AdapterProperties.class);
        when(mockedAdapterProperties.getConnectionName()).thenReturn("my_connection");

        // Create adapter subclass overriding getPropertiesFromRequest to return mockedAdapterProperties
        adapter = new DocumentAdapter(dialectMock, mockLogger) {
            @Override
            protected AdapterProperties getPropertiesFromRequest(AdapterRequest adapterRequest) {
                return mockedAdapterProperties;
            }
        };
    }

    @Test
    void testRunQuery_logsExpectedMessages() throws Exception {
        ExaMetadata exaMetadata = mock(ExaMetadata.class);

        // Mock memory limit and node count to avoid NullPointerException in calculateMaxUdfInstanceCount
        when(exaMetadata.getMemoryLimit()).thenReturn(BigInteger.valueOf(4L * 1024 * 1024 * 1024)); // 4 GB
        when(exaMetadata.getNodeCount()).thenReturn(2L);

        ExaConnectionInformation mockConnectionInfo = mock(ExaConnectionInformation.class);
        when(mockConnectionInfo.getUser()).thenReturn(null);
        when(mockConnectionInfo.getAddress()).thenReturn(null);
        // Provide a valid JSON string for password (IDENTIFIED_BY)
        when(mockConnectionInfo.getPassword()).thenReturn("{\"someKey\":\"someValue\"}");

        when(exaMetadata.getConnection("my_connection")).thenReturn(mockConnectionInfo);

        PushDownRequest request = mock(PushDownRequest.class);
        RemoteTableQuery remoteTableQuery = mock(RemoteTableQuery.class);
        QueryPlanner queryPlanner = mock(QueryPlanner.class);

        when(remoteTableQuery.toString()).thenReturn("TABLE_QUERY");
        when(exaMetadata.getScriptSchema()).thenReturn("MY_SCHEMA");
        when(dialectMock.getAdapterName()).thenReturn("my_adapter");
        when(dialectMock.getQueryPlanner(any(), any())).thenReturn(queryPlanner);

        QueryPlan queryPlan = new EmptyQueryPlan();

        when(queryPlanner.planQuery(eq(remoteTableQuery), anyInt())).thenReturn(queryPlan);

        // Call the protected runQuery via reflection
        var runQueryMethod = DocumentAdapter.class.getDeclaredMethod("runQuery", ExaMetadata.class, PushDownRequest.class, RemoteTableQuery.class);
        runQueryMethod.setAccessible(true);
        runQueryMethod.invoke(adapter, exaMetadata, request, remoteTableQuery);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Supplier<String>> captor = ArgumentCaptor.forClass(Supplier.class);
        verify(mockLogger, atLeastOnce()).fine(captor.capture());

        String allLogs = captor.getAllValues().stream().map(Supplier::get).reduce("", (a, b) -> a + "\n" + b);

        assertAll(
                () -> assertThat(allLogs, containsString("Starting to plan query | Remote table query: TABLE_QUERY")),
                () -> assertThat(allLogs, containsString("Planned query with")),
                () -> assertThat(allLogs, containsString("Script schema: 'MY_SCHEMA'")),
                () -> assertThat(allLogs, containsString("Plan type: 'EmptyQueryPlan'")),
                () -> assertThat(allLogs, containsString("Adapter: 'my_adapter'")),
                () -> assertThat(allLogs, containsString("Connection: 'my_connection'")),
                () -> assertThat(allLogs, containsString("Generated UDF call: SELECT * FROM (VALUES ()) WHERE FALSE | Remote table query: TABLE_QUERY"))
        );
    }
}