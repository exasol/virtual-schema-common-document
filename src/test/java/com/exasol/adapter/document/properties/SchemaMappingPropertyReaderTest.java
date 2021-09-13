package com.exasol.adapter.document.properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SchemaMappingPropertyReaderTest {

    @Test
    void testFileProperty(@TempDir final Path tempDir) throws IOException {
        final Path bucket = tempDir.resolve("bfsdefault/default");
        Files.createDirectories(bucket);
        Files.writeString(bucket.resolve("myMapping.json"), "test");
        final TempdirBucketFsFileResolver resolver = new TempdirBucketFsFileResolver(tempDir);
        final List<EdmlInput> edml = new SchemaMappingPropertyReader(resolver)
                .readSchemaMappingProperty("bfsdefault/default/myMapping.json");
        assertThat(edml, Matchers.contains(new EdmlInput("test", "myMapping.json")));
    }

    @Test
    void testDirectoryProperty(@TempDir final Path tempDir) throws IOException {
        final Path bucket = tempDir.resolve("bfsdefault/default");
        Files.createDirectories(bucket);
        Files.writeString(bucket.resolve("mapping1.json"), "a");
        Files.writeString(bucket.resolve("mapping2.json"), "b");
        final TempdirBucketFsFileResolver resolver = new TempdirBucketFsFileResolver(tempDir);
        final List<EdmlInput> edml = new SchemaMappingPropertyReader(resolver)
                .readSchemaMappingProperty("bfsdefault/default/");
        assertThat(edml,
                Matchers.containsInAnyOrder(new EdmlInput("a", "mapping1.json"), new EdmlInput("b", "mapping2.json")));
    }

    @Test
    void testEmptyDirectory(@TempDir final Path tempDir) throws IOException {
        final Path bucket = tempDir.resolve("bfsdefault/default");
        Files.createDirectories(bucket);
        final TempdirBucketFsFileResolver resolver = new TempdirBucketFsFileResolver(tempDir);
        final SchemaMappingPropertyReader reader = new SchemaMappingPropertyReader(resolver);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reader.readSchemaMappingProperty("bfsdefault/default/"));
        assertThat(exception.getMessage(), startsWith("E-VSD-21: No schema mapping files found in "));
    }

    @Test
    void testMappingFileDoesNotExist(@TempDir final Path tempDir) throws IOException {
        final TempdirBucketFsFileResolver resolver = new TempdirBucketFsFileResolver(tempDir);
        final SchemaMappingPropertyReader reader = new SchemaMappingPropertyReader(resolver);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reader.readSchemaMappingProperty("bfsdefault/default/"));
        assertThat(exception.getMessage(), startsWith("E-VSD-24: Failed to open mapping file"));
    }

    @Test
    void testInlineMapping() {
        final String edml = "{\"mapping\":null}";
        final List<EdmlInput> result = new SchemaMappingPropertyReader(null).readSchemaMappingProperty(edml);
        assertThat(result, Matchers.contains(new EdmlInput(edml, "inline")));
    }

    @Test
    void testInlineMappingArray() {
        final String edml1 = "{\"mapping\":null}";
        final String edml2 = "{\"source\":null}";
        final String property = "[" + edml1 + ", " + edml2 + "]";
        final List<EdmlInput> result = new SchemaMappingPropertyReader(null).readSchemaMappingProperty(property);
        assertThat(result,
                Matchers.containsInAnyOrder(new EdmlInput(edml1, "inline[0]"), new EdmlInput(edml2, "inline[1]")));
    }

    @Test
    void testEmptyProperty() {
        final SchemaMappingPropertyReader reader = new SchemaMappingPropertyReader(null);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reader.readSchemaMappingProperty(""));
        assertThat(exception.getMessage(), startsWith(
                "E-VSD-20: The property MAPPING must not be empty. Please set the MAPPING property in you virtual schema definition."));
    }
}