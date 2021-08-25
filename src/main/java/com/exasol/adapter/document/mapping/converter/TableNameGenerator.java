package com.exasol.adapter.document.mapping.converter;

import java.util.*;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.ExasolDocumentMappingLanguageException;
import com.exasol.errorreporting.ExaError;

/**
 * This class auto-generates the names for nested tables in case no name was specified in the EDML.
 */
class TableNameGenerator implements StagingTableMapping.Transformer {

    @Override
    public StagingTableMapping apply(final StagingTableMapping stagingTableMapping) {
        return modifyRecursive(stagingTableMapping, "");
    }

    private StagingTableMapping modifyRecursive(final StagingTableMapping stagingTableMapping,
            final String parentTableName) {
        final StagingTableMapping modifiedTableMapping = copyWithGeneratedNameIfRequired(stagingTableMapping,
                parentTableName);
        final List<StagingTableMapping> newNestedTables = new ArrayList<>();
        for (final StagingTableMapping nestedTable : modifiedTableMapping.getNestedTables()) {
            newNestedTables.add(modifyRecursive(nestedTable, modifiedTableMapping.getExasolName()));
        }
        return modifiedTableMapping.withNestedTables(newNestedTables);
    }

    private StagingTableMapping copyWithGeneratedNameIfRequired(final StagingTableMapping stagingTableMapping,
            final String parentTableName) {
        if (stagingTableMapping.getExasolName() == null || stagingTableMapping.getExasolName().isEmpty()) {
            final String newName = generateTableNameOrThrow(stagingTableMapping, parentTableName);
            return stagingTableMapping.withExasolName(newName);
        } else {
            return stagingTableMapping;
        }
    }

    private String generateTableNameOrThrow(final StagingTableMapping stagingTableMapping,
            final String parentTableName) {
        final DocumentPathExpression path = stagingTableMapping.getPathInRemoteTable();
        final Optional<String> property = new LastPropertyNameFinder()
                .getLastPropertyName(path.getSubPath(0, path.size() - 1));
        if (property.isPresent()) {
            return parentTableName + "_" + property.get().toUpperCase();
        } else {
            throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("F-VSD-87")
                    .message("Failed to auto-generate name for nested table {{path}}.",
                            stagingTableMapping.getPathInRemoteTable())
                    .mitigation("Please specify a destinationName in your mapping definition.").toString());
        }
    }
}
