package com.exasol.adapter.document.mapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    public static final String OPEN_LIBRARY_MAPPING = "openLibraryMapping.json";
    public static final String DATA_TYPE_TEST_MAPPING = "dataTypeTestMapping.json";
    public static final String DATA_TYPE_TEST_SRC_TABLE_NAME = "DATA_TYPE_TEST";
    public static final String DATA_TYPE_TEST_EXASOL_TABLE_NAME = "TEST";

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
     * Get a mapping from resources as file.
     *
     * @implNote This method does not use the resource directly as file, since the this method only works if the jar is
     *           unpacked. For that reason this method writes the content of the file to a temporary directory.
     *
     * @param fileName name of the mapping. Use one of the constants of this class.
     * @param tempDir  temporary directory for the file. Use @TempDir to create this directory in the test case.
     * @return temporary file with the content of the specified mapping file.
     * @throws IOException if resource was not found
     */
    public static File getMappingAsFile(final String fileName, final Path tempDir) throws IOException {
        final File tempFile = File.createTempFile("schemaTmp", ".json", tempDir.toFile());
        try (final InputStream stream = MappingTestFiles.class.getClassLoader().getResourceAsStream(fileName)) {
            Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * This method generates a invalid file from a valid using an invalidator function. It uses the org.json api because
     * it provides modifiable objects in contrast to the projects default javax.json api.
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
        try (final InputStream inputStream = getMappingAsStream(baseMappingName);
                final FileWriter fileWriter = new FileWriter(tempFile)) {
            final JSONObject baseObject = new JSONObject(new JSONTokener(inputStream));
            final JSONObject invalidObject = invalidator.apply(baseObject);
            fileWriter.write(invalidObject.toString());
            fileWriter.close();
            return tempFile;
        }
    }
}
