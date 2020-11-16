package com.exasol.adapter.document.mapping.reader.validator;

import java.util.Optional;

import org.everit.json.schema.ValidationException;

public interface ExceptionMessageImprover {

    /**
     * Get an improved error message if possible.
     * 
     * @param exception
     * @return Improved error message or {@link Optional#empty()} if the message can't be improved by this improver.
     */
    public Optional<String> tryToImprove(ValidationException exception);
}
