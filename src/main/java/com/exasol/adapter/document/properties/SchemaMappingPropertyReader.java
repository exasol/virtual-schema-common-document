package com.exasol.adapter.document.properties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.bucketfs.BucketFsFileResolver;
import com.exasol.bucketfs.BucketFsFileResolverImpl;
import com.exasol.errorreporting.ExaError;

import jakarta.json.*;

/**
 * This class resolves the {@code MAPPING} property.
 */
class SchemaMappingPropertyReader {
    private static final String BUCKETS_PREFIX = "/buckets/";
    private final BucketFsFileResolver bucketFsFileResolver;

    /**
     * Create a new instance of {@link SchemaMappingPropertyReader}.
     */
    SchemaMappingPropertyReader() {
        this(new BucketFsFileResolverImpl());
    }

    /**
     * Constructor with dependency injection for testing.
     * 
     * @param bucketFsFileResolver dependency injection for {@link BucketFsFileResolverImpl}.
     */
    SchemaMappingPropertyReader(final BucketFsFileResolver bucketFsFileResolver) {
        this.bucketFsFileResolver = bucketFsFileResolver;
    }

    /**
     * Parse the value of the {@code MAPPING} property.
     * 
     * @param schemaMappingProperty {@code MAPPING} property value
     * @return mapping definitions
     */
    List<EdmlInput> readSchemaMappingProperty(final String schemaMappingProperty) {
        if (schemaMappingProperty.isEmpty()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-20")
                    .message("The property MAPPING must not be empty.")
                    .mitigation("Please set the MAPPING property in you virtual schema definition.").toString());
        } else if (schemaMappingProperty.startsWith("{")) {
            return readInlinedMapping(schemaMappingProperty);
        } else if (schemaMappingProperty.startsWith("[")) {
            return readInlinedMappingArray(schemaMappingProperty);
        } else {
            return readMappingsFromFile(removeBucketsPrefix(schemaMappingProperty));
        }
    }

    private String removeBucketsPrefix(final String schemaMappingProperty) {
        if (schemaMappingProperty.startsWith(BUCKETS_PREFIX)) {
            return schemaMappingProperty.replaceFirst(BUCKETS_PREFIX, "/");
        } else {
            return schemaMappingProperty;
        }
    }

    private List<EdmlInput> readMappingsFromFile(final String schemaMappingParameter) {
        final List<EdmlInput> results = new ArrayList<>();
        final Path pathInBucketFs = this.bucketFsFileResolver.openFile(schemaMappingParameter);
        for (final Path path : findMappingFiles(pathInBucketFs)) {
            results.add(new EdmlInput(readSchemaMapping(path), path.getFileName().toString()));
        }
        return results;
    }

    private List<EdmlInput> readInlinedMapping(final String schemaMappingParameter) {
        return List.of(new EdmlInput(schemaMappingParameter, "inline"));
    }

    private List<EdmlInput> readInlinedMappingArray(final String schemaMappingParameter) {
        final JsonArray schemaMappings = readParameterAsJson(schemaMappingParameter);
        final List<EdmlInput> result = new ArrayList<>();
        int counter = 0;
        for (final JsonValue schemaMapping : schemaMappings) {
            result.add(new EdmlInput(schemaMappingToString(schemaMapping), "inline[" + counter + "]"));
            counter++;
        }
        return result;
    }

    private String readSchemaMapping(final Path path) {
        try {
            return Files.readString(path);
        } catch (final IOException exception) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-24")
                    .message("Failed to open mapping file {{mapping file}}.", path).toString(), exception);
        }
    }

    private String schemaMappingToString(final JsonValue schemaMapping) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final JsonWriter writer = Json.createWriter(outputStream)) {
            writer.write(schemaMapping);
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-75")
                    .message("Failed to serialize mapping file.").ticketMitigation().toString(), exception);
        }
    }

    private JsonArray readParameterAsJson(final String schemaMappingParameter) {
        try (final JsonReader reader = Json.createReader(new StringReader(schemaMappingParameter))) {
            return reader.readArray();
        }
    }

    /**
     * Find mapping files in a given path.
     * <p>
     * If the path is a file, this method returns this file. Otherwise, it returns all mapping files in this directory.
     * </p>
     *
     * @param definitionsPath path to file or directory
     * @return definition files
     */
    private List<Path> findMappingFiles(final Path definitionsPath) {
        if (Files.isDirectory(definitionsPath)) {
            return findMappingFilesInDirectory(definitionsPath);
        } else {
            return List.of(definitionsPath);
        }
    }

    private List<Path> findMappingFilesInDirectory(final Path definitionsPath) {
        try (final Stream<Path> filesStream = Files.list(definitionsPath)) {
            final String jsonFileEnding = ".json";
            final List<Path> files = filesStream.filter(entry -> !Files.isDirectory(entry))
                    .filter(file -> file.toString().endsWith(jsonFileEnding)).collect(Collectors.toList());
            if (files.isEmpty()) {
                throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-21")
                        .message("No schema mapping files found in {{MAPPINGS_FOLDER}}.", definitionsPath)
                        .mitigation(
                                "Please check that you definition files have a .json ending and are uploaded to the BucketFS path that was specified in the MAPPING property.")
                        .toString());
            }
            return files;
        } catch (final IOException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-74")
                    .message("Failed to list mapping file in given mapping folder {{MAPPINGS_FOLDER}}.",
                            definitionsPath)
                    .toString(), exception);
        }
    }
}
