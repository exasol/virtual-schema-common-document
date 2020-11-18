package com.exasol.adapter.document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.AdapterProperties;

class DocumentAdapterPropertiesTest {
    private static final String MAPPING_PATH = "/bfsdefault/default/mapping.json";
    private static final String MAPPING_KEY = "MAPPING";
    private static final String MAX_PARALLEL_UDFS_KEY = "MAX_PARALLEL_UDFS";

    @Test
    void testGetMappingDefinition() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of("MAPPING", MAPPING_PATH)));
        assertThat(properties.getMappingDefinition(), equalTo(MAPPING_PATH));
    }

    @Test
    void testGetMappingDefinitionRemovesBucketsPrefix() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of(MAPPING_KEY, "/buckets" + MAPPING_PATH)));
        assertThat(properties.getMappingDefinition(), equalTo(MAPPING_PATH));
    }

    @Test
    void testGetMappingDefinitionDoesNotRemovesPrefixStartingWithBuckets() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of(MAPPING_KEY, "/buckets-1" + MAPPING_PATH)));
        assertThat(properties.getMappingDefinition(), equalTo("/buckets-1" + MAPPING_PATH));
    }

    @Test
    void testEmptyMapping() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of(MAPPING_KEY, "")));
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                properties::getMappingDefinition);
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-20: The property MAPPING must not be empty. Please set MAPPING to the path to your schema mapping files in the BucketFS."));
    }

    @Test
    void testMissingMapping() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Collections.emptyMap()));
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                properties::getMappingDefinition);
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-72: Missing mandatory MAPPING property. Please set MAPPING to the path to your schema mapping files in the BucketFS."));
    }

    @Test
    void testGetMaxParallelUdfs() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of(MAX_PARALLEL_UDFS_KEY, "123")));
        assertThat(properties.getMaxParallelUdfs(), equalTo(123));
    }

    @Test
    void testGetMaxParallelUdfsExplicitUnlimited() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of(MAX_PARALLEL_UDFS_KEY, "-1")));
        assertThat(properties.getMaxParallelUdfs(), equalTo(Integer.MAX_VALUE));
    }

    @Test
    void testGetMaxParallelUdfsDefault() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Collections.emptyMap()));
        assertThat(properties.getMaxParallelUdfs(), equalTo(Integer.MAX_VALUE));
    }

    @Test
    void testGetMaxParallelUdfsNotNumeric() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of(MAX_PARALLEL_UDFS_KEY, "not a number")));
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                properties::getMaxParallelUdfs);
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-17: Invalid non-integer value 'not a number' for property MAX_PARALLEL_UDFS.  Please set MAX_PARALLEL_UDFS to a number >= 1 or -1 for no limit."));
    }

    @Test
    void testGetMaxParallelUdfsIllegalValue() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of(MAX_PARALLEL_UDFS_KEY, "-10")));
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                properties::getMaxParallelUdfs);
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-16: Invalid value '-10' for property MAX_PARALLEL_UDFS. Please set MAX_PARALLEL_UDFS to a number >= 1 or -1 for no limit."));
    }
}