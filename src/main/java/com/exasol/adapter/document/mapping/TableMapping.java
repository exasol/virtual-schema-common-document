package com.exasol.adapter.document.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;

/**
 * Definition of a table mapping from DynamoDB table to Exasol Virtual Schema. Each instance of this class represents a
 * table in the Exasol Virtual Schema. Typically it also represents a DynamoDB table. But it can also represent the data
 * from a nested list or object. See {@link #isRootTable()} for details.
 */
public class TableMapping implements Serializable {
    private static final long serialVersionUID = -8647175683915939405L;
    /** @serial */
    private final String exasolName;
    /** @serial */
    private final String remoteName;
    private final transient List<ColumnMapping> columns; // The columns are serialized separately in
                                                         // {@link ColumnMetadata}.
    /** @serial */
    private final DocumentPathExpression pathInRemoteTable;
    /** @serial */
    private final String additionalConfiguration;

    /**
     * Create a new instance of {@link TableMapping}
     * 
     * @param exasolName              Exasol table name
     * @param remoteName              name in the remote data source
     * @param columns                 mapped columns
     * @param pathInRemoteTable       path in remote table
     * @param additionalConfiguration additional configuration
     */
    public TableMapping(final String exasolName, final String remoteName, final List<ColumnMapping> columns,
            final DocumentPathExpression pathInRemoteTable, final String additionalConfiguration) {
        this.exasolName = exasolName;
        this.remoteName = remoteName;
        this.columns = columns;
        this.pathInRemoteTable = pathInRemoteTable;
        this.additionalConfiguration = additionalConfiguration;
    }

    /**
     * Create a new instance of {@link TableMapping} from serialized data.
     * <p>
     * The {@link #columns} are transient and for that reason must be added separately again here.
     * </p>
     * 
     * @param deserialized deserialized {@link TableMapping} (without columns since they are transient)
     * @param columns      separately deserialized columns
     */
    TableMapping(final TableMapping deserialized, final List<ColumnMapping> columns) {
        this(deserialized.exasolName, deserialized.remoteName, columns, deserialized.pathInRemoteTable,
                deserialized.additionalConfiguration);
    }

    /**
     * Get an instance of the Builder for {@link TableMapping}. This version of the builder is used for root tables.
     *
     * @param destinationName         Name of the Exasol table
     * @param remoteName              Name of the remote table that is mapped
     * @param additionalConfiguration Additional Configuration
     * @return {@link TableMapping.Builder}
     */
    public static Builder rootTableBuilder(final String destinationName, final String remoteName,
            final String additionalConfiguration) {
        final DocumentPathExpression emptyPath = DocumentPathExpression.empty();
        return new Builder(destinationName, remoteName, emptyPath, additionalConfiguration);
    }

    /**
     * Get an instance of the builder for {@link TableMapping}. This version of the builder is used to create tables
     * extracted from nested lists.
     *
     * @param destinationName         Name of the Exasol table
     * @param remoteName              Name of the remote table
     * @param pathInRemoteTable       Path expression within the document to a nested list that is mapped to a table
     * @param additionalConfiguration Additional Configuration
     * @return Builder for {@link TableMapping}
     */
    public static Builder nestedTableBuilder(final String destinationName, final String remoteName,
            final DocumentPathExpression pathInRemoteTable, final String additionalConfiguration) {
        return new Builder(destinationName, remoteName, pathInRemoteTable, additionalConfiguration);
    }

    /**
     * Get the name of the Exasol table
     * 
     * @return name of the Exasol table
     */
    public String getExasolName() {
        return this.exasolName;
    }

    /**
     * Get the name of the remote table that is mapped.
     *
     * @return name of the remote table
     */
    public String getRemoteName() {
        return this.remoteName;
    }

    /**
     * Get the path to the nested table that is mapped by this table.
     * 
     * @return path to nested list. Empty path expression if this tables maps a root document.
     */

    public DocumentPathExpression getPathInRemoteTable() {
        return this.pathInRemoteTable;
    }

    /**
     * Get the additional configuration object string.
     *
     * @return additional configuration object string.
     */

    public String getAdditionalConfiguration() {
        return this.additionalConfiguration;
    }

    /**
     * Get the columns of this table
     * 
     * @return List of {@link ColumnMapping}s
     */
    public List<ColumnMapping> getColumns() {
        return this.columns;
    }

    /**
     * Specifies if a table has a counterpart in DynamoDB
     *
     * @return {@code <true>} if this table has an pendant in DynamoDB {@code <false>} if this table represents a nested
     *         list or map from DynamoDB
     */
    public boolean isRootTable() {
        return this.pathInRemoteTable.size() == 0;
    }

    /**
     * Returns a string representation of the {@link TableMapping} instance.
     * <p>
     * This includes the Exasol table name, remote source name, column mappings,
     * path to the nested table (if any), and any additional configuration.
     *
     * @return a human-readable string describing the table mapping
     */
    @Override
    public String toString() {
        return String.format(
                "TableMapping{exasolName='%s', remoteName='%s', columns=%s, pathInRemoteTable=%s, additionalConfiguration='%s'}",
                exasolName,
                remoteName,
                columns,
                pathInRemoteTable,
                additionalConfiguration
        );
    }

    /**
     * Builder for {@link TableMapping}
     */
    public static class Builder {
        private final String exasolName;
        private final String remoteName;
        private final List<ColumnMapping> columns = new ArrayList<>();
        private final DocumentPathExpression pathToNestedTable;
        private final String additionalConfiguration;

        private Builder(final String exasolName, final String remoteName,
                final DocumentPathExpression pathToNestedTable, final String additionalConfiguration) {
            this.exasolName = exasolName;
            this.remoteName = remoteName;
            this.pathToNestedTable = pathToNestedTable;
            this.additionalConfiguration = additionalConfiguration;
        }

        /**
         * Add a {@link ColumnMapping}
         * 
         * @param columnMapping Column MappingDefinition to add
         * @return self for fluent programming interface
         */
        public Builder withColumnMappingDefinition(final ColumnMapping columnMapping) {
            this.columns.add(columnMapping);
            return this;
        }

        /**
         * Builds the {@link TableMapping}
         * 
         * @return {@link TableMapping}
         */
        public TableMapping build() {
            return new TableMapping(this.exasolName, this.remoteName, Collections.unmodifiableList(this.columns),
                    this.pathToNestedTable, this.additionalConfiguration);
        }
    }
}
