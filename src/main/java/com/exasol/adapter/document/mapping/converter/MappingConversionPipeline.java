package com.exasol.adapter.document.mapping.converter;

import java.util.List;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
import com.exasol.adapter.document.mapping.TableMapping;
import com.exasol.adapter.document.mapping.auto.SchemaInferencer;

import lombok.AllArgsConstructor;

/**
 * This class converts an EDML definition into a mapping definition (different class structure) that is more handy for
 * further processing.
 */
@AllArgsConstructor
public class MappingConversionPipeline {

    /**
     * Dependency injection of a {@link TableKeyFetcher}.
     */
    private final TableKeyFetcher tableKeyFetcher;

    private final SchemaInferencer schemaInferencer;

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
