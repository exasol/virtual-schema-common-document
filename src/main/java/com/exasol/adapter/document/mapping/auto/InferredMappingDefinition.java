package com.exasol.adapter.document.mapping.auto;

import java.util.Objects;
import java.util.Optional;

import com.exasol.adapter.document.edml.MappingDefinition;

/**
 * This represents the result of a schema auto inference returned by a {@link SchemaFetcher}. This allows auto-detecting
 * the properties of an {@link com.exasol.adapter.document.edml.EdmlDefinition}.
 * <p>
 * User defined values will have precedence over detected values, see implementation in {@link SchemaInferencer}.
 */
public class InferredMappingDefinition {

    private final MappingDefinition mapping;
    private final String additionalConfiguration;
    private final String description;

    private InferredMappingDefinition(final Builder builder) {
        this.mapping = builder.mapping;
        this.additionalConfiguration = builder.additionalConfiguration;
        this.description = builder.description;
    }

    /**
     * This defines the fields from the source and how they are converted to columns.
     *
     * @return source mapping, will never be {@code null}
     */
    public MappingDefinition getMapping() {
        return this.mapping;
    }

    /**
     * Optional additional configuration, e.g. <code>{"csv-headers": true}</code> for CSV files.
     *
     * @return optional additional configuration
     */
    public Optional<String> getAdditionalConfiguration() {
        return Optional.ofNullable(this.additionalConfiguration);
    }

    /**
     * Optional description for the destination table.
     *
     * @return optional description for the destination table
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    /**
     * Creates builder to build {@link InferredMappingDefinition}.
     *
     * @param the mandatory mapping definition
     * @return created builder
     */
    public static Builder builder(final MappingDefinition mapping) {
        return new Builder(mapping);
    }

    /**
     * Builder to build {@link InferredMappingDefinition}.
     */
    public static final class Builder {
        private final MappingDefinition mapping;
        private String additionalConfiguration;
        private String description;

        private Builder(final MappingDefinition mapping) {
            this.mapping = Objects.requireNonNull(mapping, "mapping definition");
        }

        /**
         * Builder method for additionalConfiguration parameter.
         *
         * @param additionalConfiguration field to set, e.g. <code>{"csv-headers": true}</code> for CSV files
         * @return builder
         */
        public Builder additionalConfiguration(final String additionalConfiguration) {
            this.additionalConfiguration = additionalConfiguration;
            return this;
        }

        /**
         * Builder method for description parameter.
         *
         * @param description field to set
         * @return builder
         */
        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        /**
         * Builder method of the builder.
         *
         * @return built class
         */
        public InferredMappingDefinition build() {
            return new InferredMappingDefinition(this);
        }
    }
}
