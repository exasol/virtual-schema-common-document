package com.exasol.adapter.document.mapping.converter;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.edml.KeyType;
import com.exasol.adapter.document.mapping.SourceReferenceColumnMapping;

/**
 * This class adds the source reference columns to a schema mapping.
 */
public class SourceRefColumnAdder implements StagingTableMapping.Transformer {
    private final EdmlDefinition edmlDefinition;

    /**
     * Create a new instance of {@link SourceRefColumnAdder}.
     * 
     * @param edmlDefinition EDML definition of the class to transform
     */
    public SourceRefColumnAdder(final EdmlDefinition edmlDefinition) {
        this.edmlDefinition = edmlDefinition;
    }

    @Override
    public StagingTableMapping apply(final StagingTableMapping stagingTableMapping) {
        if (this.edmlDefinition.isAddSourceReferenceColumn()) {
            return stagingTableMapping
                    .withAdditionalColumn(new ColumnWithKeyInfo(new SourceReferenceColumnMapping(), KeyType.NONE));
        } else {
            return stagingTableMapping;
        }
    }
}
