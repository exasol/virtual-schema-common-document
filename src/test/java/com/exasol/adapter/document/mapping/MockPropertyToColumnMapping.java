package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class MockPropertyToColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 5927405036134725056L;

    public MockPropertyToColumnMapping(final String destinationName, final DocumentPathExpression sourcePath,
            final MappingErrorBehaviour lookupFailBehaviour) {
        this(builder().exasolColumnName(destinationName).pathToSourceProperty(sourcePath)
                .lookupFailBehaviour(lookupFailBehaviour));
    }

    @Override
    public DataType getExasolDataType() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isExasolColumnNullable() {
        return false;
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }
}
