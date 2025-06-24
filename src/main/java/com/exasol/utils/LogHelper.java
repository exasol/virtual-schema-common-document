package com.exasol.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for logging utilities
 */
public class LogHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private LogHelper() {
    }

    /**
     * Logs a formatted message at {@link Level#FINE} if fine-level logging is enabled.
     * <p>
     * This helper method avoids unnecessary string construction (e.g., {@code String.format(...)})
     * when fine-level logging is disabled. This improves performance and suppresses static analysis
     * warnings about inefficient logging.
     * </p>
     *
     * @param logger        the {@link Logger} to log the message with
     * @param stringPattern the format string, as used by {@link String#format(String, Object...)}
     * @param args          the arguments referenced by the format specifiers in the format string;
     *                      if empty, the {@code stringPattern} is logged as-is
     */
    public static void logFine(Logger logger, final String stringPattern, final Object... args) {
        logger.fine(() -> args.length == 0 ? stringPattern : String.format(stringPattern, args));
    }
}
