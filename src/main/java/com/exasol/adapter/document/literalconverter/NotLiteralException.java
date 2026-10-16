package com.exasol.adapter.document.literalconverter;

/**
 * This Exception is thrown by {@link SqlLiteralToDocumentValueConverter} if the given SqlNode is not a literal.
 */
public class NotLiteralException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates an exception indicating that a SQL node is not a literal. */
    public NotLiteralException() {
        // Intentionally empty to enable Javadoc.
    }
}
