package com.exasol.adapter.document.mapping.auto;

import java.util.Optional;

import com.exasol.adapter.document.edml.EdmlDefinition;
import com.exasol.adapter.document.edml.MappingDefinition;

/**
 * This interface allows dialects to provide support for automatic inference of the file structure, e.g. Parquet files.
 * <p>
 * In case the user did not specify a {@link MappingDefinition mapping} in the {@link EdmlDefinition} when creating a
 * virtual schema, this class is used to automatically detect the schema of the source file(s).
 * </p>
 */
public interface SchemaFetcher {

    /**
     * Infer the schema for the given source.
     *
     * @param source              the source reference, i.e. the table name, file path or resource identifier
     * @param columnNameConverter defines how source column names are mapped to Exasol columns
     * @return the detected schema if the given source is supported or else an empty {@link Optional}
     */
    Optional<InferredMappingDefinition> fetchSchema(String source, ColumnNameConverter columnNameConverter);

    /**
     * Create a {@link SchemaFetcher} that always returns an empty {@link Optional}.
     * <p>
     * Use this method if the dialect does not support automatic schema inference.
     *
     * @return empty schema fetcher
     */
    public static SchemaFetcher empty() {
        return (source, columnNameConverter) -> Optional.empty();
    }
}
