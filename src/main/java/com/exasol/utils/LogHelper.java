package com.exasol.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for logging utilities
 */
public class LogHelper {

    /**
     * Logs a formatted message at {@link Level#FINE} if fine-level logging is enabled.
     * <p>
     * This helper method avoids unnecessary string construction (such as {@code String.format(...)})
     * when fine-level logging is not enabled. This improves performance and prevents static analysis
     * warnings related to inefficient logging.
     * </p>
     *
     * @param stringPattern the format string, as used by {@link String#format(String, Object...)}
     * @param args          the arguments referenced by the format specifiers in the format string
     */
    public static void logFine(Logger logger, final String stringPattern, final Object... args) {
        logger.fine(() -> args.length == 0 ? stringPattern : String.format(stringPattern, args));
    }
}
