package com.exasol.adapter.document.mapping.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.exasol.adapter.document.edml.ExasolDocumentMappingLanguageException;
import com.exasol.adapter.document.edml.KeyType;
import com.exasol.errorreporting.ExaError;

/**
 * This class is a validator for the keys in table mappings.
 * <p>
 * A table can either have a local or a global key. Having local and global columns is not allows.
 * </p>
 */
class DifferentKeysValidator implements StagingTableMapping.Validator {
    @Override
    public void validate(final StagingTableMapping stagingTableMapping) {
        final List<String> globalKeyNames = getColumnNames(stagingTableMapping.getKeyOfType(KeyType.GLOBAL));
        final List<String> localKeyNames = getColumnNames(stagingTableMapping.getKeyOfType(KeyType.LOCAL));
        if (!globalKeyNames.isEmpty() && !localKeyNames.isEmpty()) {
            throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("E-VSD-8").message(
                    "The table {{table name}} specified both local and global key columns: Local keys: {{local key columns}}, Global keys: {{global key columns}}. That is not allowed.",
                    stagingTableMapping.getExasolName(), localKeyNames, globalKeyNames)
                    .mitigation("Use either a local or a global key.").toString());
        }
        validateNestedTables(stagingTableMapping);
    }

    private void validateNestedTables(final StagingTableMapping stagingTableMapping) {
        for (final StagingTableMapping nestedTable : stagingTableMapping.getNestedTables()) {
            validate(nestedTable);
        }
    }

    private List<String> getColumnNames(final List<ColumnWithKeyInfo> columnsWithType) {
        return columnsWithType.stream().map(columnWithType -> columnWithType.getColumn().getExasolColumnName())
                .collect(Collectors.toList());
    }
}
