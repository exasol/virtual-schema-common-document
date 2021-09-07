package com.exasol.adapter.document.mapping.converter;

import java.util.List;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.mapping.TableKeyFetcher;
import com.exasol.adapter.document.mapping.TableMapping;

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

    /**
     * Converts an EDML definition into a mapping definition
     * 
     * @param edmlDefinition EDML definition to convert
     * @return resulting schema mapping definition
     */
    public List<TableMapping> convert(final EdmlDefinition edmlDefinition) {
        final StagingTableMapping stagingMapping = new EdmlToStagingTableMappingConverter().convert(edmlDefinition);
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
