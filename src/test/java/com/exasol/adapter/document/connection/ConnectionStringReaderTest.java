package com.exasol.adapter.document.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.ExaConnectionInformation;

class ConnectionStringReaderTest {
    final ConnectionStringReader reader = new ConnectionStringReader("http://example.com");

    @Test
    void testRead() {
        final String connectionString = "\n     {\"s3Bucket\": \"my-bucket\"}";
        final ExaConnectionInformation connectionInfo = mockConnectionInformation("", "", connectionString);
        this.reader.read(connectionInfo);
        assertThat(connectionString, equalTo(connectionString));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "test" })
    void testInvalidIdentifiedBy(final String identifiedBy) {
        final ExaConnectionInformation connectionInfo = mockConnectionInformation("", "", identifiedBy);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.reader.read(connectionInfo));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-90: Invalid connection. The 'IDENTIFIED_BY' filed must contain a JSON object with the connection details (start with '{'). Please check the user-guide at: http://example.com."));
    }

    @ParameterizedTest
    @CsvSource({ ",something", "something," })
    void testIllegalFieldSet(final String to, final String user) {
        final ExaConnectionInformation connectionInfo = mockConnectionInformation(to, user,
                "{\"s3Bucket\": \"my-bucket\"}");
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.reader.read(connectionInfo));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-89: Invalid connection. The fields 'TO' and 'USER' must be left empty. Please check the user-guide at: http://example.com."));
    }

    private ExaConnectionInformation mockConnectionInformation(final String to, final String user,
            final String identifiedBY) {
        final ExaConnectionInformation connectionInfo = mock(ExaConnectionInformation.class);
        when(connectionInfo.getAddress()).thenReturn(to);
        when(connectionInfo.getUser()).thenReturn(user);
        when(connectionInfo.getPassword()).thenReturn(identifiedBY);
        return connectionInfo;
    }
}