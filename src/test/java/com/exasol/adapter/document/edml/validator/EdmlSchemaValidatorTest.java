package com.exasol.adapter.document.edml.validator;

import static com.exasol.adapter.document.mapping.MappingTestFiles.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

import org.hamcrest.Matcher;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.adapter.document.mapping.reader.ExasolDocumentMappingLanguageException;

class EdmlSchemaValidatorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EdmlSchemaValidatorTest.class);

    private void runValidationWithResource(final String resource) throws IOException {
        final String schema = new String(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resource)).readAllBytes(),
                StandardCharsets.UTF_8);
        runValidation(schema);
    }

    private void runValidation(final String schema) {
        try {
            new EdmlSchemaValidator().validate(schema);
        } catch (final IllegalArgumentException exception) {
            LOGGER.info(exception.getMessage());
            throw exception;
        }
    }

    @Test
    void testValidBasicMapping() throws IOException {
        runValidationWithResource(BASIC_MAPPING);
    }

    @Test
    void testValidToJsonMapping() throws IOException {
        runValidationWithResource(TO_JSON_MAPPING);
    }

    @Test
    void testValidSingleColumnToTableMapping() throws IOException {
        runValidationWithResource(SINGLE_COLUMN_TO_TABLE_MAPPING);
    }

    @Test
    void testValidMultiColumnToTableMapping() throws IOException {
        runValidationWithResource(MULTI_COLUMN_TO_TABLE_MAPPING);
    }

    @Test
    void testValidWholeTableToJsonMapping() throws IOException {
        runValidationWithResource(WHOLE_TABLE_TO_TABLE_MAPPING);
    }

    private void testInvalid(final String base, final Function<JSONObject, JSONObject> invalidator,
            final Matcher<String> messageMatcher) throws IOException {
        final String invalidMapping = generateInvalid(base, invalidator);
        final ExasolDocumentMappingLanguageException exception = assertThrows(
                ExasolDocumentMappingLanguageException.class, () -> runValidation(invalidMapping));
        assertAll(
                () -> assertThat(exception.getMessage(),
                        equalTo("F-VSD-51: Syntax error in mapping definition. See causing exception for details.")),
                () -> assertThat(exception.getCause().getMessage(), messageMatcher));
    }

    @Test
    void testInvalidNoDestName() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("destinationTable");
            return base;
        }, equalTo("F-VSD-53: Syntax validation error: #: required key [destinationTable] not found."));
    }

    @Test
    void testInvalidNoSchemaSet() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("$schema");
            return base;
        }, equalTo("F-VSD-53: Syntax validation error: #: required key [$schema] not found."));
    }

    @Test
    void testInvalidWrongSchemaSet() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.put("$schema", "wrongSchema");
            return base;
        }, startsWith("F-VSD-56: Illegal value for $schema. Supported schema versions are ["));
    }

    @Test
    void testInvalidUnknownRootProperty() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.put("unknownProperty", "someValue");
            return base;
        }, equalTo("F-VSD-53: Syntax validation error: #: extraneous key [unknownProperty] is not permitted."));
    }

    @Test
    void testInvalidUnknownMappingType() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            final JSONObject isbn = base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("isbn");
            isbn.remove("toVarcharMapping");
            isbn.put("toStriiiiiiingMapping", "");
            return base;
        }, startsWith(
                "F-VSD-54: #/mapping/fields/isbn: extraneous key [toStriiiiiiingMapping] is not permitted. Use one of the following mapping definitions: ["));
    }

    @Test
    void testInvalidUnknownMappingInToTable() throws IOException {
        testInvalid(MULTI_COLUMN_TO_TABLE_MAPPING, base -> {
            final JSONObject mapping = base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("chapters")
                    .getJSONObject("toTableMapping").getJSONObject("mapping");
            mapping.remove("fields");
            mapping.put("toStriiiiingMapping", "");
            return base;
        }, startsWith(
                "F-VSD-54: #/mapping/fields/chapters/toTableMapping/mapping: extraneous key [toStriiiiingMapping] is not permitted. Use one of the following mapping definitions: ["));
    }

    @Test
    void testInvalidToTableWithNoFields() throws IOException {
        testInvalid(MULTI_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("chapters")
                    .getJSONObject("toTableMapping").getJSONObject("mapping").remove("fields");
            return base;
        }, startsWith(
                "F-VSD-52: '#/mapping/fields/chapters/toTableMapping/mapping' is empty. Specify at least one mapping. Possible mappings are ["));
    }

    @Test
    void testInvalidKeyValue() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("name")
                    .getJSONObject("toVarcharMapping").put("key", "");
            return base;
        }, equalTo(
                "F-VSD-55: #/mapping/fields/name/toVarcharMapping/key: Illegal value for property 'key'. Please set key property to 'local' or 'global'."));
    }

    @Test
    void testInvalidNoMapping() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("mapping");
            return base;
        }, equalTo("F-VSD-53: Syntax validation error: #: required key [mapping] not found."));
    }

    @Test
    void testInvalidNoFields() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.getJSONObject("mapping").remove("fields");
            return base;
        }, startsWith("F-VSD-52: '#/mapping' is empty. Specify at least one mapping. Possible mappings are ["));
    }
}
