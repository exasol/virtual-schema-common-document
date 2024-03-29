package com.exasol.adapter.document.mapping;

import static com.exasol.utils.StringSerializer.serializeToString;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.adapter.metadata.*;
import com.exasol.errorreporting.ExaError;
import com.exasol.utils.StringSerializer;

/**
 * This class converts a {@link SchemaMapping} into a {@link SchemaMetadata}. The {@link ColumnMapping}s are serialized
 * into {@link ColumnMetadata#getAdapterNotes()}. Using {@link #convertBackColumn(ColumnMetadata)} it can get
 * deserialized again.
 */
public class SchemaMappingToSchemaMetadataConverter {
    private static final Logger LOG = Logger.getLogger(SchemaMappingToSchemaMetadataConverter.class.getName());

    /**
     * Create a {@link SchemaMetadata} for a given {@link SchemaMapping}
     *
     * @param schemaMapping the {@link SchemaMapping} to be converted
     * @return {@link SchemaMetadata}
     */
    public SchemaMetadata convert(final SchemaMapping schemaMapping) {
        final List<TableMetadata> tableMetadata = new ArrayList<>();
        /* The HashMap is used here instead of the List interface because it is serializable. */
        final HashMap<String, TableMapping> tableMappings = new HashMap<>();
        for (final TableMapping table : schemaMapping.getTableMappings()) {
            tableMetadata.add(convertTable(table));
            tableMappings.put(table.getExasolName(), table);
        }
        final String adapterNotes = serializeTableMapping(tableMappings);
        return new SchemaMetadata(adapterNotes, tableMetadata);
    }

    private String serializeTableMapping(final HashMap<String, TableMapping> tableMappings) {
        try {
            return serializeToString(new TableMappings(tableMappings));
        } catch (final IOException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-25")
                    .message("Failed to serialize TableMapping.").ticketMitigation().toString(), exception);
        }
    }

    private TableMetadata convertTable(final TableMapping tableMapping) {
        LOG.fine(() -> "Creating virtual table " + tableMapping.getExasolName() + " with remote name "
                + tableMapping.getRemoteName() + " and " + tableMapping.getColumns().size() + " columns");
        final List<ColumnMetadata> columnDefinitions = new ArrayList<>();
        for (final ColumnMapping column : tableMapping.getColumns()) {
            columnDefinitions.add(convertColumn(column));
        }
        final String adapterNotes = "";// Due to a bug in exasol core adapter notes are not stored for tables -> this
                                       // got fixed!
        return new TableMetadata(tableMapping.getExasolName(), adapterNotes, columnDefinitions, "");
    }

    /**
     * Create a {@link ColumnMetadata} for a given {@link ColumnMapping}.
     * 
     * @param columnMapping to convert
     * @return {@link ColumnMetadata}
     */
    public ColumnMetadata convertColumn(final ColumnMapping columnMapping) {
        LOG.fine(() -> "  - Column " + columnMapping.getExasolColumnName() + ": " + columnMapping.getExasolDataType()
                + ", " + (columnMapping.isExasolColumnNullable() ? "NULLABLE" : "NOT NULL"));
        return ColumnMetadata.builder()//
                .name(columnMapping.getExasolColumnName())//
                .type(columnMapping.getExasolDataType())//
                .defaultValue("NULL")//
                .nullable(columnMapping.isExasolColumnNullable())//
                .adapterNotes(serialize(columnMapping)).build();
    }

    private String serialize(final ColumnMapping columnMapping) {
        try {
            return serializeToString(columnMapping);
        } catch (final IOException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-26")
                    .message("Failed to serialize ColumnMapping.").ticketMitigation().toString(), exception);
        }
    }

    /**
     * Deserializes a {@link TableMapping} from {@link TableMetadata}.
     *
     * <p>
     * We use the adapter notes of the schema here since in the past there was a bug (SPOT-9952) that did not allow us
     * to store the metadata with the table. However we did not change it now since in the future it might make sense to
     * move also the Column metadata into the Schema metadata. By that we would have all persistent information in one
     * place. That makes testing this part a lot easier. Another reason is that it has no downside to do it that way and
     * for that reason is not worth a refactoring.
     * </p>
     * 
     * @param tableMetadata      metadata for the table to be deserialized
     * @param schemaAdapterNotes adapter notes of the schema
     * @return deserialized {@link TableMapping}
     * @throws IllegalStateException if deserialization fails
     */
    public TableMapping convertBackTable(final TableMetadata tableMetadata, final String schemaAdapterNotes) {
        try {
            return convertBackTableIntern(tableMetadata, schemaAdapterNotes);
        } catch (final IOException | ClassNotFoundException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-57")
                    .message("Failed to deserialize TableMappingDefinition.").ticketMitigation().toString(), exception);
        }
    }

    private TableMapping convertBackTableIntern(final TableMetadata tableMetadata, final String schemaAdapterNotes)
            throws IOException, ClassNotFoundException {
        final TableMapping preliminaryTable = findTableInSchemaMetadata(tableMetadata.getName(), schemaAdapterNotes);
        /*
         * As the columns are transient in TableMappingDefinition, they must be deserialized from the ColumnMetadata and
         * added separately.
         */
        final List<ColumnMapping> columns = new ArrayList<>(tableMetadata.getColumns().size());
        for (final ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
            columns.add(convertBackColumn(columnMetadata));
        }
        return new TableMapping(preliminaryTable, columns);
    }

    /**
     * See comment on {@link #convertBackTable(TableMetadata, String)}
     */
    private TableMapping findTableInSchemaMetadata(final String tableName, final String schemaAdapterNotes)
            throws IOException, ClassNotFoundException {
        final TableMappings tableMappings = (TableMappings) StringSerializer.deserializeFromString(schemaAdapterNotes);
        return tableMappings.mappings.get(tableName);
    }

    /**
     * Deserializes a {@link ColumnMapping} from {@link ColumnMetadata}.
     *
     * @param columnMetadata {@link ColumnMetadata} to deserialized from
     * @return ColumnMappingDefinition
     * @throws IllegalStateException if deserialization fails
     */
    public ColumnMapping convertBackColumn(final ColumnMetadata columnMetadata) {
        try {
            final String serialized = columnMetadata.getAdapterNotes();
            return (ColumnMapping) StringSerializer.deserializeFromString(serialized);
        } catch (final IOException | ClassNotFoundException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-VSD-58")
                    .message("Failed to deserialize ColumnMappingDefinition.").ticketMitigation().toString(),
                    exception);
        }
    }

    /**
     * This class is used as a fix for the bug because of which {@link TableMetadata} can't store adapter notes. See:
     * https://github.com/exasol/dynamodb-virtual-schema/issues/25. It gets serialized in the {@link SchemaMetadata} and
     * stores a map that gives the {@link TableMapping} for its Exasol table name.
     */
    private static class TableMappings implements Serializable {
        private static final long serialVersionUID = 5951740683817686929L;
        private final HashMap<String, TableMapping> mappings;

        private TableMappings(final HashMap<String, TableMapping> mappings) {
            this.mappings = mappings;
        }
    }
}
