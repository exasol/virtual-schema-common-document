package com.exasol.adapter.document.mapping.reader;

import static com.exasol.adapter.document.mapping.MappingTestFiles.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import org.hamcrest.Matcher;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.adapter.document.mapping.reader.validator.JsonSchemaMappingValidator;

class JsonSchemaMappingValidatorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaMappingValidatorTest.class);
    @TempDir
    Path tempDir;

    void runValidation(final File file) {
        final JsonSchemaMappingValidator jsonSchemaMappingValidator = new JsonSchemaMappingValidator();
        try {
            jsonSchemaMappingValidator.validate(file);
        } catch (final IllegalArgumentException exception) {
            LOGGER.info(exception.getMessage());
            throw exception;
        }
    }

    @Test
    void testValidBasicMapping() throws IOException {
        runValidation(getMappingAsFile(BASIC_MAPPING, this.tempDir));
    }

    @Test
    void testValidToJsonMapping() throws IOException {
        runValidation(getMappingAsFile(TO_JSON_MAPPING, this.tempDir));
    }

    @Test
    void testValidSingleColumnToTableMapping() throws IOException {
        runValidation(getMappingAsFile(SINGLE_COLUMN_TO_TABLE_MAPPING, this.tempDir));
    }

    @Test
    void testValidMultiColumnToTableMapping() throws IOException {
        runValidation(getMappingAsFile(MULTI_COLUMN_TO_TABLE_MAPPING, this.tempDir));
    }

    @Test
    void testValidWholeTableToJsonMapping() throws IOException {
        runValidation(getMappingAsFile(WHOLE_TABLE_TO_TABLE_MAPPING, this.tempDir));
    }

    private void testInvalid(final String base, final Function<JSONObject, JSONObject> invalidator,
            final Matcher<String> messageMatcher) throws IOException {
        final File invalidFile = generateInvalidFile(base, invalidator, this.tempDir);
        final ExasolDocumentMappingLanguageException exception = assertThrows(
                ExasolDocumentMappingLanguageException.class,
                () -> runValidation(invalidFile));
        assertAll(
                () -> assertThat(exception.getMessage(),
                        equalTo("E-VSD-EDML-1: Syntax error in mapping definition '" + invalidFile.getName()
                                + "'. See causing exception for details.")),
                () -> assertThat(exception.getCause().getMessage(), messageMatcher));
    }

    @Test
    void testInvalidNoDestName() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("destinationTable");
            return base;
        }, equalTo("E-VSD-EDML-3: Syntax validation error: #: required key [destinationTable] not found."));
    }

    @Test
    void testInvalidNoSchemaSet() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("$schema");
            return base;
        }, equalTo("E-VSD-EDML-3: Syntax validation error: #: required key [$schema] not found."));
    }

    @Test
    void testInvalidWrongSchemaSet() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.put("$schema", "wrongSchema");
            return base;
        }, startsWith("E-VSD-EDML-6: Illegal value for $schema. Supported schema versions are ["));
    }

    @Test
    void testInvalidUnknownRootProperty() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.put("unknownProperty", "someValue");
            return base;
        }, equalTo("E-VSD-EDML-3: Syntax validation error: #: extraneous key [unknownProperty] is not permitted."));
    }

    @Test
    void testInvalidUnknownMappingType() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            final JSONObject isbn = base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("isbn");
            isbn.remove("toVarcharMapping");
            isbn.put("toStriiiiiiingMapping", "");
            return base;
        }, startsWith(
                "E-VSD-EDML-4: #/mapping/fields/isbn: extraneous key [toStriiiiiiingMapping] is not permitted. Use one of the following mapping definitions: ["));
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
                "E-VSD-EDML-4: #/mapping/fields/chapters/toTableMapping/mapping: extraneous key [toStriiiiingMapping] is not permitted. Use one of the following mapping definitions: ["));
    }

    @Test
    void testInvalidToTableWithNoFields() throws IOException {
        testInvalid(MULTI_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("chapters")
                    .getJSONObject("toTableMapping").getJSONObject("mapping").remove("fields");
            return base;
        }, startsWith(
                "E-VSD-EDML-2: '#/mapping/fields/chapters/toTableMapping/mapping' is empty. Specify at least one mapping. Possible mappings are ["));
    }

    @Test
    void testInvalidKeyValue() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("name")
                    .getJSONObject("toVarcharMapping").put("key", "");
            return base;
        }, equalTo(
                "E-VSD-EDML-5: #/mapping/fields/name/toVarcharMapping/key: Illegal value for property 'key'. Please set key property to 'local' or 'global'."));
    }

    @Test
    void testInvalidNoMapping() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("mapping");
            return base;
        }, equalTo("E-VSD-EDML-3: Syntax validation error: #: required key [mapping] not found."));
    }

    @Test
    void testInvalidNoFields() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.getJSONObject("mapping").remove("fields");
            return base;
        }, startsWith("E-VSD-EDML-2: '#/mapping' is empty. Specify at least one mapping. Possible mappings are ["));
    }
}
