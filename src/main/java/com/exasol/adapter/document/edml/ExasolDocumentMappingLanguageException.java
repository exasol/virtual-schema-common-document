package com.exasol.adapter.document.edml;

/**
 * Exception that is thrown on mapping failures.
 */
public class ExasolDocumentMappingLanguageException extends RuntimeException {
    private static final long serialVersionUID = 6015275477199239149L;

    /**
     * Create an instance of {@link ExasolDocumentMappingLanguageException}.
     *
     * @param message Exception message
     */
    public ExasolDocumentMappingLanguageException(final String message) {
        super(message);
    }

    /**
     * Create an instance of {@link ExasolDocumentMappingLanguageException}.
     *
     * @param message Exception message
     * @param cause   Exception cause
     */
    public ExasolDocumentMappingLanguageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
