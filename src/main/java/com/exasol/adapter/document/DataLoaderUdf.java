package com.exasol.adapter.document;

import java.io.IOException;

import com.exasol.*;
import com.exasol.adapter.document.documentfetcher.DocumentFetcher;
import com.exasol.adapter.document.mapping.SchemaMapper;

/**
 * This interface is the basis for the database-specific UDF call.
 * <p>
 * In the UDF call, the document data is fetched by the {@link DocumentFetcher}, mapped by the {@link SchemaMapper} and
 * finally emitted to the Exasol database.
 * </p>
 * <p>
 * To save memory and process huge amounts of data, this task is implemented as a pipeline. That means that fetching,
 * mapping and emitting of the rows is done for each row and not en-block.
 * </p>
 */
public interface DataLoaderUdf {

    /**
     * Run the import and process the document data.
     * <p>
     * The input rows for this UDF are generated by the {@link UdfCallBuilder}. Each row contains a different
     * {@link DocumentFetcher}. Each {@link DocumentFetcher} can then generates again many result rows.
     * </p>
     *
     * @param exaMetadata Exasol metadata
     * @param exaIterator Exasol iterator containing the input rows and used for emitting the result rows
     * @throws ClassNotFoundException       if deserialization fails
     * @throws ExaIterationException        on illegal access on the iterator
     * @throws ExaDataTypeException         if input data types are wrong (should not happen if called by
     *                                      {@link UdfCallBuilder})
     * @throws IOException                  if deserialization fails
     * @throws ExaConnectionAccessException if accessing the connection information is not allowed
     */
    void run(final ExaMetadata exaMetadata, final ExaIterator exaIterator) throws ClassNotFoundException,
            ExaIterationException, ExaDataTypeException, IOException, ExaConnectionAccessException;
}