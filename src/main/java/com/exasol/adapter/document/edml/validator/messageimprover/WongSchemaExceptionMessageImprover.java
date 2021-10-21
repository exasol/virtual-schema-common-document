package com.exasol.adapter.document.edml.validator.messageimprover;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.everit.json.schema.*;

import com.exasol.adapter.document.edml.validator.ExceptionMessageImprover;
import com.exasol.errorreporting.ExaError;

/**
 * This {@link ExceptionMessageImprover} improves the exception message for unsupported values for {@code $schema}.
 */
public class WongSchemaExceptionMessageImprover extends AbstractExceptionMessageImprover {
    @Override
    public Optional<String> tryToImprove(final ValidationException exception) {
        if (exception.getMessage().startsWith("#/$schema:")
                && exception.getMessage().endsWith("is not a valid enum value")) {
            return Optional.of(ExaError.messageBuilder("F-VSD-56").message("Illegal value for $schema.")
                    .mitigation("Supported schema versions are {{SUPPORTED_SCHEMA}}")
                    .parameter("SUPPORTED_SCHEMA", getAvailableSchemas(exception.getViolatedSchema())).toString());
        } else {
            return Optional.empty();
        }
    }

    private List<String> getAvailableSchemas(final Schema violatedSchema) {
        final EnumSchema enumSchema = (EnumSchema) violatedSchema;
        return enumSchema.getPossibleValues().stream().map(String.class::cast).collect(Collectors.toList());
    }
}
