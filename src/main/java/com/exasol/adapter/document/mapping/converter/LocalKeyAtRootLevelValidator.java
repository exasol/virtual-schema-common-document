package com.exasol.adapter.document.mapping.converter;

import com.exasol.adapter.document.edml.ExasolDocumentMappingLanguageException;
import com.exasol.adapter.document.edml.KeyType;
import com.exasol.errorreporting.ExaError;

/**
 * This class validates that a definition has no local keys in a root-table mapping since those make no sense.
 */
class LocalKeyAtRootLevelValidator implements StagingTableMapping.Validator {
    @Override
    public void validate(final StagingTableMapping stagingTableMapping) {
        for (final ColumnWithKeyInfo columnWithKeyInfo : stagingTableMapping.getColumns()) {
            if (KeyType.LOCAL.equals(columnWithKeyInfo.getKey())) {
                throw new ExasolDocumentMappingLanguageException(ExaError.messageBuilder("E-VSD-47").message(
                        "Invalid local key for column {{column}}. Local keys make no sense in root table mapping definitions.",
                        columnWithKeyInfo.getColumn().getExasolColumnName()).mitigation("Please make this key global.")
                        .toString());
            }
        }
    }
}
