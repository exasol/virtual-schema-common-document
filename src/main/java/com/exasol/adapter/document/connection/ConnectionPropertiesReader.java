package com.exasol.adapter.document.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Optional;

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
        final Optional<String> result = readString(propertyName);
        if (result.isEmpty()) {
            throw getMissingPropertyException(propertyName);
        } else {
            return result.get();
        }
    }

    /**
     * Read a JSON formatted property.
     * 
     * @param propertyName name of the property
     * @return JSON bytes
     */
    public byte[] readRequiredJsonProperty(final String propertyName) {
        final JsonValue propertyValue = this.input.get(propertyName);
        if (propertyValue == null) {
            throw getMissingPropertyException(propertyName);
        } else {
            return toJson(propertyValue);
        }
    }

    private byte[] toJson(final JsonValue propertyValue) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (final JsonWriter jsonWriter = Json.createWriter(outputStream)) {
                jsonWriter.write(propertyValue);
            }
            return outputStream.toByteArray();
        } catch (final IOException exception) {
            throw new UncheckedIOException(
                    ExaError.messageBuilder("E-VSD-98").message("Failed to re-serialize JSON property.").toString(),
                    exception);
        }
    }

    /**
     * Read a non required string property.
     * 
     * @param propertyName name of the property to read
     * @return optional property value
     */
    public Optional<String> readString(final String propertyName) {
        final JsonValue result = this.input.get(propertyName);
        if (result == null) {
            return Optional.empty();
        }
        if (!(result instanceof JsonString)) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-91").message(
                    "Invalid connection. The value of the property {{property name}} must be of type string (written in quotes).",
                    propertyName).mitigation(USER_GUIDE_MITIGATION, this.userGuideUrl).toString());
        }
        final String stringResult = ((JsonString) result).getString();
        if (stringResult.isBlank()) {
            return Optional.empty();
        } else {
            return Optional.of(stringResult);
        }
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
