package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.metadata.DataType;

import lombok.experimental.SuperBuilder;

/**
 * This class defines a mapping that extracts a decimal number from the remote document and maps it to an Exasol
 * {@code DECIMAL} column.
 */
@SuperBuilder(toBuilder = true)
public final class PropertyToDecimalColumnMapping extends AbstractPropertyToNumberColumnMapping {
    private static final long serialVersionUID = -8263709400720209080L;
    /** @serial */
    private final int decimalPrecision;
    /** @serial */
    private final int decimalScale;

    public int getDecimalScale() {
        return decimalScale;
    }

    public int getDecimalPrecision() {
        return decimalPrecision;
    }

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

    @Override
    public String toString() {
        return "PropertyToDecimalColumnMapping(super=" + super.toString() + ", decimalPrecision="
                + this.getDecimalPrecision() + ", decimalScale=" + this.getDecimalScale() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(decimalPrecision, decimalScale);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyToDecimalColumnMapping other = (PropertyToDecimalColumnMapping) obj;
        return decimalPrecision == other.decimalPrecision && decimalScale == other.decimalScale;
    }

}
