package com.exasol.adapter.document.mapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

    private final List<File> tempFiles = new ArrayList<>();

    public static InputStream getMappingAsStream(final String fileName) {
        return MappingTestFiles.class.getClassLoader().getResourceAsStream(fileName);
    }

    public static File getMappingAsFile(final String fileName) {
        return new File(MappingTestFiles.class.getClassLoader().getResource(fileName).getFile());
    }

    /**
     * This method generates a invalid file from a valid using an invalidator function. It uses the org.json api because
     * it provides modifiable objects in contrast to the projects default javax.json api.
     *
     * @param baseMappingName Definition to use as basis
     * @param invalidator     Function that modifies / invalidates the definition.
     * @return File containing modified definition.
     * @throws IOException on read or write error.
     */
    public File generateInvalidFile(final String baseMappingName, final Function<JSONObject, JSONObject> invalidator)
            throws IOException {
        final File tempFile = File.createTempFile("schemaTmp", ".json");
        this.tempFiles.add(tempFile);
        try (final InputStream inputStream = getMappingAsStream(baseMappingName);
                final FileWriter fileWriter = new FileWriter(tempFile)) {
            final JSONObject baseObject = new JSONObject(new JSONTokener(inputStream));
            final JSONObject invalidObject = invalidator.apply(baseObject);
            fileWriter.write(invalidObject.toString());
            fileWriter.close();
            return tempFile;
        }
    }

    public void deleteAllTempFiles() {
        for (final File tempFile : this.tempFiles) {
            tempFile.delete();
        }
        this.tempFiles.clear();
    }
}
