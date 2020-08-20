package com.exasol.adapter.document.mapping.reader;

import static com.exasol.adapter.document.mapping.MappingTestFiles.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.adapter.document.mapping.MappingTestFiles;

class JsonSchemaMappingValidatorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSchemaMappingValidatorTest.class);
    private final MappingTestFiles mappingTestFiles = new MappingTestFiles();

    void runValidation(final File file) throws IOException {
        final JsonSchemaMappingValidator jsonSchemaMappingValidator = new JsonSchemaMappingValidator();
        try {
            jsonSchemaMappingValidator.validate(file);
        } catch (final IllegalArgumentException exception) {
            LOGGER.info(exception.getMessage());
            throw exception;
        }
    }

    @AfterEach
    void afterEach() {
        this.mappingTestFiles.deleteAllTempFiles();
    }

    @Test
    void testValidBasicMapping() throws IOException {
        runValidation(getMappingAsFile(BASIC_MAPPING));
    }

    @Test
    void testValidToJsonMapping() throws IOException {
        runValidation(getMappingAsFile(TO_JSON_MAPPING));
    }

    @Test
    void testValidSingleColumnToTableMapping() throws IOException {
        runValidation(getMappingAsFile(SINGLE_COLUMN_TO_TABLE_MAPPING));
    }

    @Test
    void testValidMultiColumnToTableMapping() throws IOException {
        runValidation(getMappingAsFile(MULTI_COLUMN_TO_TABLE_MAPPING));
    }

    @Test
    void testValidWholeTableToJsonMapping() throws IOException {
        runValidation(getMappingAsFile(WHOLE_TABLE_TO_TABLE_MAPPING));
    }

    private void testInvalid(final String base, final Function<JSONObject, JSONObject> invalidator,
            final String expectedMessage) throws IOException {
        final File invalidFile = this.mappingTestFiles.generateInvalidFile(base, invalidator);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> runValidation(invalidFile));
        assertThat(exception.getMessage(),
                equalTo("Syntax error in " + invalidFile.getName() + ": " + expectedMessage));
    }

    @Test
    void testInvalidNoDestName() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("destinationTable");
            return base;
        }, "#: required key [destinationTable] not found");
    }

    @Test
    void testInvalidNoSchemaSet() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("$schema");
            return base;
        }, "#: required key [$schema] not found");
    }

    @Test
    void testInvalidWrongSchemaSet() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.put("$schema", "wrongSchema");
            return base;
        }, "#/$schema $schema must be set  to https://raw.githubusercontent.com/exasol/virtual-schema-common-document/master/src/main/resources/schemas/edml/v1.json");
    }

    @Test
    void testInvalidUnknownRootProperty() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.put("unknownProperty", "someValue");
            return base;
        }, "#: extraneous key [unknownProperty] is not permitted");
    }

    @Test
    void testInvalidUnknownMappingType() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            final JSONObject isbn = base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("isbn");
            isbn.remove("toVarcharMapping");
            isbn.put("toStriiiiiiingMapping", "");
            return base;
        }, "#/mapping/fields/isbn: extraneous key [toStriiiiiiingMapping] is not permitted, use one of the following mapping definitions: toVarcharMapping, toTableMapping, toDecimalMapping, toJsonMapping, fields");
    }

    @Test
    void testInvalidToTableWithNoFields() throws IOException {
        testInvalid(MULTI_COLUMN_TO_TABLE_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("chapters")
                    .getJSONObject("toTableMapping").getJSONObject("mapping").remove("fields");
            return base;
        }, "#/mapping/fields/chapters/toTableMapping/mapping Please specify at least one mapping. Possible are: toVarcharMapping, toTableMapping, toDecimalMapping, toJsonMapping, fields");
    }

    @Test
    void testInvalidKeyValue() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("name")
                    .getJSONObject("toVarcharMapping").put("key", "");
            return base;
        }, "#/mapping/fields/name/toVarcharMapping/key: Please set key property to 'local' or 'global'.");
    }

    @Test
    void testInvalidNoMapping() throws IOException {
        testInvalid(BASIC_MAPPING, base -> {
            base.remove("mapping");
            return base;
        }, "#: required key [mapping] not found");
    }

    @Test
    void testInvalidUnknownMappingInToTable() throws IOException {
        testInvalid(MULTI_COLUMN_TO_TABLE_MAPPING, base -> {
            final JSONObject mapping = base.getJSONObject("mapping").getJSONObject("fields").getJSONObject("chapters")
                    .getJSONObject("toTableMapping").getJSONObject("mapping");
            mapping.remove("fields");
            mapping.put("toStriiiiingMapping", "");
            return base;
        }, "#/mapping/fields/chapters/toTableMapping/mapping: extraneous key [toStriiiiingMapping] is not permitted, use one of the following mapping definitions: toVarcharMapping, toTableMapping, toDecimalMapping, toJsonMapping, fields");
    }
}
