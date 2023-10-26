package com.exasol.adapter.document.mapping.converter;

import java.util.List;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
import com.exasol.adapter.document.mapping.TableMapping;
import com.exasol.adapter.document.mapping.auto.SchemaInferencer;

/**
 * This class converts an EDML definition into a mapping definition (different class structure) that is more handy for
 * further processing.
 */
public class MappingConversionPipeline {

    private final TableKeyFetcher tableKeyFetcher;

    private final SchemaInferencer schemaInferencer;

    /**
     * Create a new instance of {@link MappingConversionPipeline}.
     * 
     * @param tableKeyFetcher  Dependency injection of a {@link TableKeyFetcher}
     * @param schemaInferencer dependency injection of a {@link SchemaInferencer}
     */
    public MappingConversionPipeline(final TableKeyFetcher tableKeyFetcher, final SchemaInferencer schemaInferencer) {
        this.tableKeyFetcher = tableKeyFetcher;
        this.schemaInferencer = schemaInferencer;
    }

    /**
     * Converts an EDML definition into a mapping definition
     *
     * @param edmlDefinition EDML definition to convert
     * @return resulting schema mapping definition
     */
    // transformation pipeline on the mappings
    public List<TableMapping> convert(final EdmlDefinition edmlDefinition) {
        final EdmlDefinition enrichedDefinition = this.schemaInferencer.inferSchema(edmlDefinition);
        final StagingTableMapping stagingMapping = new EdmlToStagingTableMappingConverter().convert(enrichedDefinition);
        return stagingMapping//
                .transformedBy(new ColumnNameGenerator())//
                .transformedBy(new TableNameGenerator())//
                .validateBy(new DifferentKeysValidator())//
                .validateBy(new LocalKeyAtRootLevelValidator())//
                .transformedBy(new KeyAdder(this.tableKeyFetcher))//
                .transformedBy(new ForeignKeyAdder())//
                .transformedBy(new SourceRefColumnAdder(edmlDefinition))//
                .asToTableMappings();
    }
}
