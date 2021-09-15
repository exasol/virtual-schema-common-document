package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a decimal number from the remote document and maps it to an Exasol DECIMAL
 * column.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public final class PropertyToDecimalColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 6_021_806_680_404_016_342L;//
    /** @serial */
    private final int decimalPrecision;
    /** @serial */
    private final int decimalScale;
    /** @serial */
    private final MappingErrorBehaviour overflowBehaviour;
    /** @serial */
    private final MappingErrorBehaviour notNumericBehaviour;

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createDecimal(this.decimalPrecision, this.decimalScale);
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }
}
