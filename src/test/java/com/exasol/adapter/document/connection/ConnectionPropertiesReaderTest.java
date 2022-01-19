package com.exasol.adapter.document.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConnectionPropertiesReaderTest {
    private static final String USER_GUIDE_EXAMPLE = "http://example.com";
    private static final String EMPTY_JSON_OBJECT = "{ }";

    @Test
    void testReadRequiredString() {
        final ConnectionPropertiesReader reader = new ConnectionPropertiesReader("{\"key\": \"value\"}",
                USER_GUIDE_EXAMPLE);
        assertThat(reader.readRequiredString("key"), equalTo("value"));
    }

    @Test
    void testReadString() {
        final ConnectionPropertiesReader reader = new ConnectionPropertiesReader("{\"key\": \"value\"}",
                USER_GUIDE_EXAMPLE);
        assertThat(reader.readString("key"), equalTo("value"));
    }

    @Test
    void testReadStringDefault() {
        final ConnectionPropertiesReader reader = new ConnectionPropertiesReader(EMPTY_JSON_OBJECT, USER_GUIDE_EXAMPLE);
        assertThat(reader.readString("key"), equalTo(null));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testReadBoolean(final boolean value) {
        final ConnectionPropertiesReader reader = new ConnectionPropertiesReader("{\"key\": " + value + "}",
                USER_GUIDE_EXAMPLE);
        assertThat(reader.readBooleanWithDefault("key", false), equalTo(value));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testReadBooleanDefault(final boolean defaultValue) {
        final ConnectionPropertiesReader reader = new ConnectionPropertiesReader(EMPTY_JSON_OBJECT, USER_GUIDE_EXAMPLE);
        assertThat(reader.readBooleanWithDefault("key", defaultValue), equalTo(defaultValue));
    }

    @Test
    void testRequiredStringMissing() {
        final ConnectionPropertiesReader reader = new ConnectionPropertiesReader(EMPTY_JSON_OBJECT, USER_GUIDE_EXAMPLE);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reader.readRequiredString("key"));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-93: Invalid connection. The connection definition does not specify the required property 'key'. Please check the user-guide at: 'http://example.com'."));
    }

    @Test
    void testWrongDataTypeForString() {
        final ConnectionPropertiesReader reader = new ConnectionPropertiesReader("{\"key\": 123}", USER_GUIDE_EXAMPLE);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reader.readRequiredString("key"));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-91: Invalid connection. The value of the property 'key' must be of type string (written in quotes). Please check the user-guide at: 'http://example.com'."));
    }

    @Test
    void testWrongDataTypeForBoolean() {
        final ConnectionPropertiesReader reader = new ConnectionPropertiesReader("{\"key\": 123}", USER_GUIDE_EXAMPLE);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reader.readBooleanWithDefault("key", true));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-92: Invalid connection. The value of the property 'key' must be of type boolean (true or false without quotes). Please check the user-guide at: 'http://example.com'."));
    }

    @Test
    void testInvalidJsonSyntax() {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new ConnectionPropertiesReader("{", USER_GUIDE_EXAMPLE));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-94: Invalid connection. The connection definition has a invalid syntax. Please check the user-guide at: 'http://example.com'."));
    }
}