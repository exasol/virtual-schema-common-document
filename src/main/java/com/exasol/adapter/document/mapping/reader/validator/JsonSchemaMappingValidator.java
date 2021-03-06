package com.exasol.adapter.document.mapping.reader.validator;

import java.io.*;
import java.util.List;
import java.util.Optional;

import org.everit.json.schema.*;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.exasol.adapter.document.mapping.reader.ExasolDocumentMappingLanguageException;
import com.exasol.adapter.document.mapping.reader.validator.messageimprover.*;
import com.exasol.errorreporting.ExaError;

/**
 * Validator for mapping definitions using a JSON-schema validator.
 * <p>
 * The validator in this packages requires the use of the {@code io.json} API instead of the project-wide {@code javax}
 * API.
 * </p>
 */
public class JsonSchemaMappingValidator {
    private static final String MAPPING_LANGUAGE_SCHEMA = "schemas/edml-1.2.0.json";
    private static final List<ExceptionMessageImprover> EXCEPTION_MESSAGE_IMPROVER = List.of(
            new UnknownKeyTypeExceptionMessageImprover(), new UnknownMappingExceptionMessageImprover(),
            new NoMappingExceptionMessageImprover(), new WongSchemaExceptionMessageImprover());
    private final Schema schema;

    /**
     * Create an instance of {@link JsonSchemaMappingValidator}.
     */
    public JsonSchemaMappingValidator() {
        final ClassLoader classLoader = JsonSchemaMappingValidator.class.getClassLoader();
        try (final InputStream inputStream = classLoader.getResourceAsStream(MAPPING_LANGUAGE_SCHEMA)) {
            final JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            this.schema = SchemaLoader.load(rawSchema);
        } catch (final IOException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-22")
                    .message("Internal error (Failed to open EDML-schema from resources).").ticketMitigation()
                    .toString(), exception);
        }
    }

    /**
     * Validates the schema from given file using a JSON-schema validator.
     * 
     * @param schemaMappingDefinition schema mapping definition to validate
     * @throws ExasolDocumentMappingLanguageException if schema is violated
     */
    public void validate(final File schemaMappingDefinition) {
        try (final InputStream inputStream = new FileInputStream(schemaMappingDefinition)) {
            final JSONObject definitionObject = new JSONObject(new JSONTokener(inputStream));
            validate(definitionObject, schemaMappingDefinition.getName());
        } catch (final IOException exception) {
            throw new IllegalArgumentException(
                    ExaError.messageBuilder("E-VSD-23").message("Failed to open mapping file {{MAPPING_FILE}}.")
                            .parameter("MAPPING_FILE", schemaMappingDefinition).toString(),
                    exception);
        }
    }

    private void validate(final JSONObject schemaMappingDefinition, final String fileName) {
        try {
            final Validator validator = Validator.builder().build();
            validator.performValidation(this.schema, schemaMappingDefinition);
        } catch (final ValidationException originalException) {
            throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("F-VSD-51")
                    .message("Syntax error in mapping definition {{MAPPING_FILE}}.")
                    .mitigation("See causing exception for details.").parameter("MAPPING_FILE", fileName).toString(),
                    makeValidationExceptionMoreReadable(originalException, fileName));
        }
    }

    private ExasolDocumentMappingLanguageException makeValidationExceptionMoreReadable(
            final ValidationException exception, final String fileName) {
        final List<ValidationException> causingExceptions = exception.getCausingExceptions();
        if (!causingExceptions.isEmpty()) {
            final ValidationException firstException = causingExceptions.get(0);
            return makeValidationExceptionMoreReadable(firstException, fileName);
        } else {
            for (final ExceptionMessageImprover improver : EXCEPTION_MESSAGE_IMPROVER) {
                final Optional<String> improveResult = improver.tryToImprove(exception);
                if (improveResult.isPresent()) {
                    return new ExasolDocumentMappingLanguageException(improveResult.get());
                }
            }
            return new ExasolDocumentMappingLanguageException(
                    ExaError.messageBuilder("F-VSD-53").message("Syntax validation error: {{VALIDATION_ERROR|uq}}.")
                            .parameter("VALIDATION_ERROR", exception.getMessage()).toString());
        }
    }
}
