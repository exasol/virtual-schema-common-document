package com.exasol.adapter.document.mapping;

/**
 * This class generates an excerpt of a string.
 */
public class ExcerptGenerator {
    /**
     * Get an excerpt of a string value for error reporting.
     *
     * @param value string value
     * @return excerpt if string was longer than 50 chars. Otherwise the original string.
     */
    public static String getExcerpt(final String value) {
        if (value.length() > 50) {
            return value.substring(0, 50) + "...";
        } else {
            return value;
        }
    }
}
