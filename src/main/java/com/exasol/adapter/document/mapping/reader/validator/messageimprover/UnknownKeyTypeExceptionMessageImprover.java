package com.exasol.adapter.document.mapping.reader.validator.messageimprover;

import java.util.Optional;

import org.everit.json.schema.ValidationException;

import com.exasol.errorreporting.ExaError;

public class UnknownKeyTypeExceptionMessageImprover extends AbstractExceptionMessageImprover {
    @Override
    public Optional<String> tryToImprove(final ValidationException exception) {
        if (exception.getPointerToViolation().endsWith("/key")
                && exception.getMessage().endsWith("is not a valid enum value")) {
            return Optional.of(ExaError.messageBuilder("E-VSD-EDML-5")
                    .message("{{VIOLATION_POINTER}}: Illegal value for property 'key'.")
                    .unquotedParameter("VIOLATION_POINTER", exception.getPointerToViolation(),
                            "Pointer to the property in the document that cause the validation error.")
                    .mitigation("Please set key property to 'local' or 'global'.").toString());
        } else {
            return Optional.empty();
        }
    }
}
