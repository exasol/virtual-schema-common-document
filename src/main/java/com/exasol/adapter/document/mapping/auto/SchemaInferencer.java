package com.exasol.adapter.document.mapping.auto;

import java.util.Optional;
import java.util.logging.Logger;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.errorreporting.ExaError;

/**
 * This class automatically infers the schema for an {@link EdmlDefinition} in case the user did not specify a
 * {@link EdmlDefinition#getMapping() mapping} when creating the virtual schema.
 */
public class SchemaInferencer {
    private static final Logger LOG = Logger.getLogger(SchemaInferencer.class.getName());
    private final SchemaFetcher schemaFetcher;

    /**
     * Create a new instance.
     *
     * @param schemaFetcher the fetcher for retrieving the schema
     */
    public SchemaInferencer(final SchemaFetcher schemaFetcher) {
        this.schemaFetcher = schemaFetcher;
    }

    /**
     * There are two ways to provide a mapping:
     * <ol>
     * <li>If the {@link EdmlDefinition} contains a {@link EdmlDefinition#getMapping() mapping} provided upfront then
     * this method will return it unchanged.</li>
     * <li>If the {@link EdmlDefinition} does not contains a {@link EdmlDefinition#getMapping() mapping}, this method
     * will infer a mapping from the {@link EdmlDefinition#getMapping() source} using the {@link SchemaFetcher} and add
     * it to the {@link EdmlDefinition}.
     * <p>
     * In case the {@link SchemaFetcher} also detected destination table name, description or additional configuration,
     * these will also be used in the returned mapping. User defined values however take precedence.</li>
     * </ol>
     *
     * @param edmlDefinition the {@link EdmlDefinition} from which to get or infer the mapping
     * @return {@link EdmlDefinition}, either unchanged or with added mapping
     * @throws IllegalStateException    in case mapping inference fails
     * @throws IllegalArgumentException in case the current VSD dialect does not support mapping inference
     */
    public EdmlDefinition inferSchema(final EdmlDefinition edmlDefinition) {
        if (edmlDefinition.getMapping() != null) {
            LOG.finest("Mapping defined, no need to infer it.");
            return edmlDefinition;
        }
        LOG.finest(() -> "Mapping not defined, infer it from source " + edmlDefinition.getSource());
        final Optional<InferredMappingDefinition> detectedSchema = fetchSchema(edmlDefinition.getSource());
        if (detectedSchema.isEmpty()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-101")
                    .message("This virtual schema does not support auto inference for source {{source|q}}.")
                    .parameter("source", edmlDefinition.getSource(),
                            "Value of the SOURCE parameter specified when creating the virtual schema")
                    .mitigation("Please specify the 'mapping' element in the JSON EDML definition.").toString());
        }
        LOG.finest(() -> "Detected mapping for source " + edmlDefinition.getSource() + ": " + detectedSchema.get());
        final EdmlDefinition updatedMapping = copyWithMapping(edmlDefinition, detectedSchema.get());
        LOG.finest(() -> "Updated mapping: " + updatedMapping);
        return updatedMapping;
    }

    private Optional<InferredMappingDefinition> fetchSchema(final String source) {
        try {
            return this.schemaFetcher.fetchSchema(source);
        } catch (final RuntimeException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-102")
                    .message("Schema auto inference for source {{source|q}} failed.")
                    .mitigation("Make sure that the input files exist at {{source|q}}")
                    .mitigation("See cause error message for details.")
                    .mitigation(
                            "Fix the root cause or specify the 'mapping' element in the JSON EDML definition to skip auto inference.")
                    .parameter("source", source,
                            "Value of the SOURCE parameter specified when creating the virtual schema")
                    .toString(), exception);
        }
    }

    private EdmlDefinition copyWithMapping(final EdmlDefinition edmlDefinition,
            final InferredMappingDefinition inferredMapping) {
        final String additionalConfiguration = getAdditionalConfiguration(edmlDefinition, inferredMapping);
        final String description = getDescription(edmlDefinition, inferredMapping);
        return EdmlDefinition.builder() //
                .additionalConfiguration(additionalConfiguration)
                .addSourceReferenceColumn(edmlDefinition.isAddSourceReferenceColumn()) //
                .description(description) //
                .destinationTable(edmlDefinition.getDestinationTable()) //
                .source(edmlDefinition.getSource()) //
                .mapping(inferredMapping.getMapping()) //
                .build();
    }

    private String getDescription(final EdmlDefinition edmlDefinition,
            final InferredMappingDefinition inferredMapping) {
        final String existingDescription = edmlDefinition.getDescription();
        if ((existingDescription != null) && !existingDescription.isBlank()) {
            LOG.finest(() -> "Using existing description '" + existingDescription + "'");
            return existingDescription;
        }
        LOG.finest(() -> "Using detected description " + inferredMapping.getDescription());
        return inferredMapping.getDescription().orElse(null);
    }

    private String getAdditionalConfiguration(final EdmlDefinition edmlDefinition,
            final InferredMappingDefinition inferredMapping) {
        final String existingConfiguration = edmlDefinition.getAdditionalConfiguration();
        if ((existingConfiguration != null) && !existingConfiguration.isBlank()) {
            LOG.finest(() -> "Using existing additional configuration '" + existingConfiguration + "'");
            return existingConfiguration;
        }
        LOG.finest(() -> "Using detected additional configuration " + inferredMapping.getAdditionalConfiguration());
        return inferredMapping.getAdditionalConfiguration().orElse(null);
    }
}
