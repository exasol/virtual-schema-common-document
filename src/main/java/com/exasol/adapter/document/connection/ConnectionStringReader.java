package com.exasol.adapter.document.connection;

import com.exasol.ExaConnectionInformation;
import com.exasol.errorreporting.ExaError;

/**
 * This class reads the connection string (value of IDENTIFIED BY) from an Exasol connection definition.
 */
public class ConnectionStringReader {
    private static final String USER_GUIDE_MITIGATION = "Please check the user-guide at: {{user guide url|uq}}.";
    private final String userGuideUrl;

    public ConnectionStringReader(final String userGuideUrl) {
        this.userGuideUrl = userGuideUrl;
    }

    /**
     * Read connection string.
     * 
     * @param connectionInformation connection object
     * @return JSON string
     */
    public String read(final ExaConnectionInformation connectionInformation) {
        final String user = connectionInformation.getUser();
        final String to = connectionInformation.getAddress();
        final String identifiedBy = connectionInformation.getPassword();
        if (isSet(user) || isSet(to)) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-89")
                    .message("Invalid connection. The fields 'TO' and 'USER' must be left empty.")
                    .mitigation(USER_GUIDE_MITIGATION, this.userGuideUrl).toString());
        }
        if (!isSet(identifiedBy) || !looksLikeJsonObject(identifiedBy)) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-90").message(
                    "Invalid connection. The 'IDENTIFIED_BY' filed must contain a JSON object with the connection details (start with '{').")
                    .mitigation(USER_GUIDE_MITIGATION, this.userGuideUrl).toString());
        }
        return identifiedBy;
    }

    private boolean looksLikeJsonObject(final String identifiedBy) {
        return identifiedBy.trim().startsWith("{");
    }

    private boolean isSet(final String input) {
        return input != null && !input.isBlank();
    }
}
