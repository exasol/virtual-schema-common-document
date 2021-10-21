package com.exasol.adapter.document.edml.validator.messageimprover;

import java.util.Optional;
import java.util.Set;

import org.everit.json.schema.ValidationException;

import com.exasol.adapter.document.edml.validator.ExceptionMessageImprover;
import com.exasol.errorreporting.ExaError;

/**
 * This {@link ExceptionMessageImprover} improves exception message of the JSON schema validator for mapping definitions
 * where they don't specify a mapping for a property.
 */
public class NoMappingExceptionMessageImprover extends AbstractExceptionMessageImprover {
    @Override
    public Optional<String> tryToImprove(final ValidationException exception) {
        if (exception.getPointerToViolation().endsWith("/mapping") && exception.getKeyword().equals("minProperties")) {
            final Set<String> possibleMappings = possibleObjectProperties(exception.getViolatedSchema());
            return Optional.of(ExaError.messageBuilder("F-VSD-52")//
                    .message("{{DOC_POINTER}} is empty.")
                    .mitigation("Specify at least one mapping. Possible mappings are {{POSSIBLE_MAPPINGS}}.")
                    .parameter("DOC_POINTER", exception.getPointerToViolation(),
                            "Pointer to the invalid definition inside the document.")
                    .parameter("POSSIBLE_MAPPINGS", possibleMappings).toString());
        } else {
            return Optional.empty();
        }
    }
}
