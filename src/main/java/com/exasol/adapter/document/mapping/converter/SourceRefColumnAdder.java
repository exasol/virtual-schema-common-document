package com.exasol.adapter.document.mapping.converter;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.edml.KeyType;
import com.exasol.adapter.document.mapping.SourceReferenceColumnMapping;

import lombok.AllArgsConstructor;

/**
 * This class adds the source reference columns to a schema mapping.
 */
@AllArgsConstructor
public class SourceRefColumnAdder implements StagingTableMapping.Transformer {
    /**
     * EDML definition of the class to transform
     */
    private final EdmlDefinition edmlDefinition;

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
