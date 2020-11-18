package com.exasol.adapter.document.mapping.reader.validator;

import java.util.Optional;

import org.everit.json.schema.ValidationException;

/**
 * Classes implementing this interface improve schema validation error messages.
 * <p>
 * Each implementing class implements a different improvement. The {@link JsonSchemaMappingValidator} only uses the
 * first fitting improvement.
 * </p>
 */
public interface ExceptionMessageImprover {

    /**
     * Get an improved error message if possible.
     *
     * @param exception exception to improve
     * @return Improved error message or {@link Optional#empty()} if the message can't be improved by this improver.
     */
    public Optional<String> tryToImprove(ValidationException exception);
}
