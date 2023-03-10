package com.exasol.adapter.document.mapping.auto;

import java.util.Optional;
import java.util.logging.Logger;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.edml.MappingDefinition;
import com.exasol.errorreporting.ExaError;

/**
 * This class automatically infers the schema for an {@link EdmlDefinition} in case the user did not specify a
 * {@link EdmlDefinition#getMapping() mapping} in.
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
     * If the given {@link EdmlDefinition} does not contain a {@link EdmlDefinition#getMapping() mapping}, this will
     * return a new {@link MappingDefinition} with an automatically generated mapping based on the source. If a
     * {@link EdmlDefinition#getMapping() mapping} is present, this will return the {@link EdmlDefinition} unmodified.
     * 
     * @param edmlDefinition the {@link EdmlDefinition} for which to infer the schema if it is missing.
     * @return the updated {@link EdmlDefinition}.
     */
    public EdmlDefinition inferSchema(final EdmlDefinition edmlDefinition) {
        if (edmlDefinition.getMapping() != null) {
            return edmlDefinition;
        }
        final Optional<MappingDefinition> detectedSchema = fetchSchema(edmlDefinition.getSource());
        if (detectedSchema.isEmpty()) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-VSD-101")
                    .message("This virtual schema does not support auto inference for source {{source|q}}.")
                    .parameter("source", "Value of the SOURCE parameter specified when creating the virtual schema")
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
                    .parameter("source", source,
                            "Value of the SOURCE parameter specified when creating the virtual schema")
                    .mitigation("Make sure that the input files exist at {{source|q}}", source)
                    .mitigation("See cause error message for details.")
                    .mitigation(
                            "Fix the root cause or specify the 'mapping' element in the JSON EDML definition to skip auto inference.")
                    .toString(), exception);
        }
    }

    private EdmlDefinition copyWithMapping(final EdmlDefinition edmlDefinition,
            final MappingDefinition autoInferenceMapping) {
        return EdmlDefinition.builder() //
                .additionalConfiguration(edmlDefinition.getAdditionalConfiguration())
                .addSourceReferenceColumn(edmlDefinition.isAddSourceReferenceColumn())
                .description(edmlDefinition.getDescription()) //
                .destinationTable(edmlDefinition.getDestinationTable()) //
                .source(edmlDefinition.getSource()) //
                .mapping(autoInferenceMapping) //
                .build();
    }
}
