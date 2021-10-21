package com.exasol.adapter.document.literalconverter;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.sql.SqlNode;

/**
 * This is an interface for converters that convert an Exasol literal into to a {@link DocumentNode}.
 */
@java.lang.SuppressWarnings("squid:S119") // VisitorType does not fit naming conventions.
public interface SqlLiteralToDocumentValueConverter {
    /**
     * Convert an Exasol literal into to a {@link DocumentNode}.
     * 
     * @param exasolLiteralNode exasol literal to convert
     * @return converted {@link DocumentNode}.
     * @throws NotLiteralException if the given {@link SqlNode} is no literal
     */
    DocumentNode convert(SqlNode exasolLiteralNode) throws NotLiteralException;
}
