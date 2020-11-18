package com.exasol.adapter.document;

import com.exasol.adapter.AdapterProperties;
import com.exasol.errorreporting.ExaError;

/**
 * This class adds document specific properties to {@link AdapterProperties}.
 */
public class DocumentAdapterProperties {
    private static final String MAPPING_KEY = "MAPPING";
    private static final String MAX_PARALLEL_UDFS_KEY = "MAX_PARALLEL_UDFS";
    public static final String BUCKETS_PREFIX = "/buckets";
    private final AdapterProperties properties;

    /**
     * Create a new instance of {@link DocumentAdapterProperties}.
     * 
     * @param properties Adapter Properties
     */
    public DocumentAdapterProperties(final AdapterProperties properties) {
        this.properties = properties;
    }

    /**
     * Check if the mapping definition property is set.
     *
     * @return {@code true} if schema definition property is set
     */
    public boolean hasMappingDefinition() {
        return this.properties.containsKey(MAPPING_KEY);
    }

    /**
     * Get mapping definition property.
     *
     * @return String path to mapping definition files in the BucketFS
     */
    public String getMappingDefinition() {
        if (!hasMappingDefinition()) {
            throw new IllegalArgumentException(
                    ExaError.messageBuilder("E-VSD-72").message("Missing mandatory MAPPING property.")
                            .mitigation("Please set MAPPING to the path to your schema mapping files in the BucketFS.")
                            .toString());
        }
        final String property = this.properties.get(MAPPING_KEY);
        if (property.isEmpty()) {
            throw new IllegalArgumentException(
                    ExaError.messageBuilder("E-VSD-20").message("The property MAPPING must not be empty.")
                            .mitigation("Please set MAPPING to the path to your schema mapping files in the BucketFS.")
                            .toString());
        }
        if (property.startsWith(BUCKETS_PREFIX)) {
            return property.replaceFirst(BUCKETS_PREFIX, "");
        } else {
            return property;
        }
    }

    /**
     * Get MAX_PARALLEL_UDFS property value.
     *
     * @return configured maximum number of UDFs that are executed in parallel. default: -1
     */
    public int getMaxParallelUdfs() {
        final String propertyValue = this.properties.get(MAX_PARALLEL_UDFS_KEY);
        final int integerValue = readMaxParallelUdfs(propertyValue);
        if (integerValue == -1) {
            return Integer.MAX_VALUE;
        } else if (integerValue >= 1) {
            return integerValue;
        } else {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-16")
                    .message("Invalid value {{VALUE}} for property MAX_PARALLEL_UDFS.")
                    .parameter("VALUE", propertyValue)
                    .mitigation("Please set MAX_PARALLEL_UDFS to a number >= 1 or -1 for no limit.")
                    .toString());
        }
    }

    private int readMaxParallelUdfs(final String propertyValue) {
        if (propertyValue == null) {
            return Integer.MAX_VALUE;
        } else {
            try {
                return Integer.parseInt(propertyValue);
            } catch (final NumberFormatException exception) {
                throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-17")
                        .message("Invalid non-integer value {{VALUE}} for property MAX_PARALLEL_UDFS. ")
                        .parameter("VALUE", propertyValue)
                        .mitigation("Please set MAX_PARALLEL_UDFS to a number >= 1 or -1 for no limit.")
                        .toString());
            }
        }
    }
}
