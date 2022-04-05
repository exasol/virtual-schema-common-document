package com.exasol.adapter.document.edml.deserializer;

import java.util.*;
import java.util.stream.Collectors;

import com.exasol.errorreporting.ExaError;

import jakarta.json.JsonObject;

class DeserializationHelper {
    private DeserializationHelper() {
        // static class
    }

    static <T extends Enum<T>> Optional<T> readEnum(final JsonObject json, final String key, final Class<T> theEnum) {
        final T[] values = theEnum.getEnumConstants();
        if (json.containsKey(key)) {
            final String value = json.getString(key);
            final Optional<T> found = Arrays.stream(values).filter(each -> each.name().equalsIgnoreCase(value))
                    .findAny();
            if (found.isEmpty()) {
                final List<String> allowedValues = Arrays.stream(values).map(Enum::name).collect(Collectors.toList());
                throw new IllegalStateException(ExaError.messageBuilder("E-VSD-104")
                        .message("Invalid value {{value}} for property {{key}}.", value, key)
                        .mitigation("Please use one of the following values: {{allowed values}}.", allowedValues)
                        .toString());
            } else {
                return found;
            }
        } else {
            return Optional.empty();
        }
    }

    static String readRequiredString(final JsonObject json, final String key) {
        final String value = json.getString(key);
        if (value == null) {
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-101")
                    .message("Invalid EDML definition. Missing required property {{key}}.", key).toString());
        }
        return value;
    }

    static Optional<Integer> readOptionalInt(final JsonObject json, final String key) {
        if (json.containsKey(key)) {
            return Optional.of(json.getInt(key));
        } else {
            return Optional.empty();
        }
    }

    static Optional<Boolean> readOptionalBoolean(final JsonObject json, final String key) {
        if (json.containsKey(key)) {
            return Optional.of(json.getBoolean(key));
        } else {
            return Optional.empty();
        }
    }
}
