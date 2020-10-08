package com.exasol.adapter.document;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import com.exasol.ExaConnectionInformation;
import com.exasol.adapter.document.mapping.SchemaMappingRequest;
import com.exasol.sql.expression.ValueExpression;

/**
 * Interface for classes that implement the data loading. Classes implementing this interface get serialized and
 * transferred to the UDF.There the {@link UdfEntryPoint} invokes
 * {@link #run(ExaConnectionInformation,SchemaMappingRequest)}.
 */
public interface DataLoader extends Serializable {

    /**
     * Run the data loading.
     * 
     * @param connectionInformation connection definition
     * @param schemaMappingRequest  request for the schema mapping
     *
     * @return Stream of Exasol rows.
     */
    public Stream<List<ValueExpression>> run(final ExaConnectionInformation connectionInformation,
            final SchemaMappingRequest schemaMappingRequest);
}
