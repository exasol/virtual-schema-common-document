package com.exasol.adapter.document.connection;

import java.io.StringReader;

import com.exasol.errorreporting.ExaError;

import jakarta.json.*;

/**
 * This class reads connection properties from a JSON string.
 */
public class ConnectionPropertiesReader {
    private static final String USER_GUIDE_MITIGATION = "Please check the user-guide at: {{user guide url|uq}}.";
    private final String userGuideUrl;
    private final JsonObject input;

    /**
     * Create a new instance of {@link ConnectionPropertiesReader}.
     * 
     * @param jsonString   JSON string to read
     * @param userGuideUrl link to the user guide (for exception messages)
     */
    public ConnectionPropertiesReader(final String jsonString, final String userGuideUrl) {
        this.userGuideUrl = userGuideUrl;
        this.input = readConnectionJson(jsonString);
    }

    /**
     * Read a required string property.
     * 
     * @param propertyName property name
     * @return property value
     */
    public String readRequiredString(final String propertyName) {
        final String result = readString(propertyName);
        if (result == null || result.isBlank()) {
            throw getMissingPropertyException(propertyName);
        }
        return result;
    }

    /**
     * Read a non required string property.
     * 
     * @param propertyName name of the property to read
     * @return property value
     */
    public String readString(final String propertyName) {
        final JsonValue result = this.input.get(propertyName);
        if (result == null) {
            return null;
        }
        if (!(result instanceof JsonString)) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-91").message(
                    "Invalid connection. The value of the property {{property name}} must be of type string (written in quotes).",
                    propertyName).mitigation(USER_GUIDE_MITIGATION, this.userGuideUrl).toString());
        }
        return ((JsonString) result).getString();
    }

    /**
     * Read a boolean property.
     * 
     * @param propertyName name of the property
     * @param defaultValue default value to use if property is not set
     * @return property value
     */
    public boolean readBooleanWithDefault(final String propertyName, final boolean defaultValue) {
        final JsonValue result = this.input.get(propertyName);
        if (result == null) {
            return defaultValue;
        }
        final JsonValue.ValueType valueType = result.getValueType();
        if (valueType.equals(JsonValue.ValueType.TRUE)) {
            return true;
        } else if (valueType.equals(JsonValue.ValueType.FALSE)) {
            return false;
        } else {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-92").message(
                    "Invalid connection. The value of the property {{property name}} must be of type boolean (true or false without quotes).",
                    propertyName).mitigation(USER_GUIDE_MITIGATION, this.userGuideUrl).toString());
        }
    }

    private IllegalArgumentException getMissingPropertyException(final String propertyName) {
        return new IllegalArgumentException(ExaError.messageBuilder("E-VSD-93").message(
                "Invalid connection. The connection definition does not specify the required property {{property name}}.",
                propertyName).mitigation(USER_GUIDE_MITIGATION, this.userGuideUrl).toString());
    }

    private JsonObject readConnectionJson(final String connectionJson) {
        try (final StringReader stringReader = new StringReader(connectionJson);
                final JsonReader jsonReader = Json.createReader(stringReader)) {
            return jsonReader.readObject();
        } catch (final Exception exception) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-94")
                    .message("Invalid connection. The connection definition has a invalid syntax.")
                    .mitigation(USER_GUIDE_MITIGATION, this.userGuideUrl).toString(), exception);
        }
    }
}
