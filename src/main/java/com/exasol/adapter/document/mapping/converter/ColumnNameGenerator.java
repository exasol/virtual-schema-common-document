package com.exasol.adapter.document.mapping.converter;

import java.util.Optional;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.ExasolDocumentMappingLanguageException;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.PropertyToColumnMapping;
import com.exasol.errorreporting.ExaError;

/**
 * This class tries to auto-generate the names for columns where no name is set.
 */
class ColumnNameGenerator extends AbstractColumnModifier {
    @Override
    protected ColumnMapping modify(final ColumnMapping column) {
        final String oldName = column.getExasolColumnName();
        if (oldName == null || oldName.isEmpty()) {
            return column.withNewExasolName(generateColumnNameOrThrow(column));
        } else {
            return column;
        }
    }

    private String generateColumnNameOrThrow(final ColumnMapping columnMapping) {
        if (columnMapping instanceof PropertyToColumnMapping) {
            final PropertyToColumnMapping propertyToColumnMapping = (PropertyToColumnMapping) columnMapping;
            final DocumentPathExpression path = propertyToColumnMapping.getPathToSourceProperty();
            final Optional<String> defaultColumnName = new LastPropertyNameFinder().getLastPropertyName(path);
            if (defaultColumnName.isPresent()) {
                return defaultColumnName.get().toUpperCase();
            } else {
                throw getCantGenerateException(path.toString());
            }
        } else {
            throw getCantGenerateException(columnMapping.toString());
        }
    }

    private ExasolDocumentMappingLanguageException getCantGenerateException(final String column) {
        return new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("F-VSD-49")
                .message("Can't auto-generate a column name for the mapping of {{column}}.", column)
                .mitigation("Please set destinationName for this mapping.").toString());
    }
}
