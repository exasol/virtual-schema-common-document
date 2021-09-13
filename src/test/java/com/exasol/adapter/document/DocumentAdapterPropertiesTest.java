package com.exasol.adapter.document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.document.properties.DocumentAdapterProperties;
import com.exasol.adapter.document.properties.EdmlInput;

class DocumentAdapterPropertiesTest {
    private static final String MAPPING_KEY = "MAPPING";
    private static final String MAX_PARALLEL_UDFS_KEY = "MAX_PARALLEL_UDFS";
    private static final String A_MAPPING = "{\"source\":null}";

    @Test
    void testGetMappingDefinition() {
        final DocumentAdapterProperties properties = new DocumentAdapterProperties(
                new AdapterProperties(Map.of(MAPPING_KEY, A_MAPPING)));
        assertThat(properties.getMappingDefinition(), contains(new EdmlInput(A_MAPPING, "inline")));
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