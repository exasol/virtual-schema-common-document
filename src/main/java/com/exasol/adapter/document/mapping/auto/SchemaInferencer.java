package com.exasol.adapter.document.mapping.auto;

import java.util.Optional;
import java.util.logging.Logger;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.edml.MappingDefinition;
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
     * <li>Either the {@link EdmlDefinition} contains an explicit mapping provided upfront or</li>
     * <li>infer the mapping from the {@link EdmlDefinition#getMapping() source data}.</li>
     * </ol>
     * If the given {@link EdmlDefinition} contains a {@link EdmlDefinition#getMapping() mapping}, the method will
     * return this. Otherwise the method infers a mapping from the {@link EdmlDefinition#getMapping() source} and adds
     * this to the {@link EdmlDefinition}. In both cases the method returns the {@link EdmlDefinition}.
     * 
     * @param edmlDefinition the {@link EdmlDefinition} from which to get or infer the mapping
     * @return {@link EdmlDefinition}, either unchanged or with added mapping
     * @throws IllegalStateException    in case mapping inference fails
     * @throws IllegalArgumentException in case the current VSD dialect does not support mapping inference
     */
    public EdmlDefinition inferSchema(final EdmlDefinition edmlDefinition) {
        if (edmlDefinition.getMapping() != null) {
            return edmlDefinition;
        }
        final Optional<MappingDefinition> detectedSchema = fetchSchema(edmlDefinition.getSource());
        if (detectedSchema.isEmpty()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-101")
                    .message("This virtual schema does not support auto inference for source {{source|q}}.")
                    .parameter("source", edmlDefinition.getSource(),
                            "Value of the SOURCE parameter specified when creating the virtual schema")
                    .mitigation("Please specify the 'mapping' element in the JSON EDML definition.").toString());
        }
        LOG.fine(() -> "Detected mapping for source " + edmlDefinition.getSource() + ": " + detectedSchema.get());
        return copyWithMapping(edmlDefinition, detectedSchema.get());
    }

    private Optional<MappingDefinition> fetchSchema(final String source) {
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

    private EdmlDefinition copyWithMapping(final EdmlDefinition edmlDefinition, final MappingDefinition mapping) {
        return EdmlDefinition.builder() //
                .additionalConfiguration(edmlDefinition.getAdditionalConfiguration())
                .addSourceReferenceColumn(edmlDefinition.isAddSourceReferenceColumn())
                .description(edmlDefinition.getDescription()) //
                .destinationTable(edmlDefinition.getDestinationTable()) //
                .source(edmlDefinition.getSource()) //
                .mapping(mapping) //
                .build();
    }
}
