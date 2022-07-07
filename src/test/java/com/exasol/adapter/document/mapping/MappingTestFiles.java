package com.exasol.adapter.document.mapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

import org.json.JSONObject;
import org.json.JSONTokener;

public class MappingTestFiles {

    public static final String BASIC_MAPPING = "basicMapping.json";
    public static final String TO_JSON_MAPPING = "toJsonMapping.json";
    public static final String SINGLE_COLUMN_TO_TABLE_MAPPING = "singleColumnToTableMapping.json";
    public static final String MULTI_COLUMN_TO_TABLE_MAPPING = "multiColumnToTableMapping.json";
    public static final String WHOLE_TABLE_TO_TABLE_MAPPING = "wholeTableToJsonMapping.json";
    public static final String DOUBLE_NESTED_TO_TABLE_MAPPING = "doubleNestedToTableMapping.json";

    /**
     * Private constructor to hide the public default.
     */
    private MappingTestFiles() {
        // empty on purpose
    }

    public static InputStream getMappingAsStream(final String fileName) {
        return MappingTestFiles.class.getClassLoader().getResourceAsStream(fileName);
    }

    /**
     * Get a mapping from resources as string.
     *
     * @param fileName name of the mapping. Use one of the constants of this class.
     * @return temporary file with the content of the specified mapping file.
     */
    public static String getMappingAsString(final String fileName) {
        try (final InputStream stream = MappingTestFiles.class.getClassLoader().getResourceAsStream(fileName)) {
            return new String(Objects.requireNonNull(stream).readAllBytes(), StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * This method generates a invalid file from a valid using an invalidator function. It uses the org.json api because
     * it provides modifiable objects in contrast to the projects default jakarta.json api.
     *
     * @param baseMappingName Definition to use as basis
     * @param invalidator     Function that modifies / invalidates the definition.
     * @param tempDir         temporary directory for the modified file. use @TempDir to create this directory in the
     *                        test case.
     * @return File containing modified definition.
     * @throws IOException on read or write error.
     */
    public static File generateInvalidFile(final String baseMappingName,
            final Function<JSONObject, JSONObject> invalidator, final Path tempDir) throws IOException {
        final File tempFile = File.createTempFile("schemaTmp", ".json", tempDir.toFile());
        try (final FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write(generateInvalid(baseMappingName, invalidator));
            fileWriter.close();
            return tempFile;
        }
    }

    public static String generateInvalid(final String baseMappingName,
            final Function<JSONObject, JSONObject> invalidator) throws IOException {
        try (final InputStream inputStream = getMappingAsStream(baseMappingName)) {
            final JSONObject baseObject = new JSONObject(new JSONTokener(inputStream));
            final JSONObject invalidObject = invalidator.apply(baseObject);
            return invalidObject.toString();
        }
    }
}
