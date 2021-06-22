package com.exasol.adapter.document.mapping.reader.validator.messageimprover;

import java.util.Optional;
import java.util.Set;

import org.everit.json.schema.ValidationException;

import com.exasol.errorreporting.ExaError;

public class UnknownMappingExceptionMessageImprover extends AbstractExceptionMessageImprover {

    @Override
    public Optional<String> tryToImprove(final ValidationException exception) {
        if (exception.getErrorMessage().startsWith("extraneous key")
                && exception.getSchemaLocation().equals("#/definitions/mappingDefinition")) {
            return improveMessage(exception);
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> improveMessage(final ValidationException exception) {
        final Set<String> possibleDefinitions = possibleObjectProperties(exception.getViolatedSchema());
        if (!possibleDefinitions.isEmpty()) {
            return Optional.of(ExaError.messageBuilder("E-VSD-EDML-4").message("{{VALIDATION_ERROR|uq}}.")
                    .parameter("VALIDATION_ERROR", exception.getMessage())
                    .mitigation("Use one of the following mapping definitions: {{POSSIBLE_DEFINITIONS}}.")
                    .parameter("POSSIBLE_DEFINITIONS", possibleDefinitions).toString());
        } else {
            return Optional.empty();
        }
    }
}
