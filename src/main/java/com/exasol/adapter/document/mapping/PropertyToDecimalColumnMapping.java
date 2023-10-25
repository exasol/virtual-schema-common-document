package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.metadata.DataType;

/**
 * This class defines a mapping that extracts a decimal number from the remote document and maps it to an Exasol
 * {@code DECIMAL} column.
 */
public final class PropertyToDecimalColumnMapping extends AbstractPropertyToNumberColumnMapping {
    private static final long serialVersionUID = -8263709400720209080L;
    /** @serial */
    private final int decimalPrecision;
    /** @serial */
    private final int decimalScale;

    int getDecimalScale() {
        return decimalScale;
    }

    int getDecimalPrecision() {
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

    public abstract static class PropertyToDecimalColumnMappingBuilder<C extends PropertyToDecimalColumnMapping, B extends PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<C, B>>
            extends AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder<C, B> {
        private int decimalPrecision;
        private int decimalScale;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder
                    .fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToDecimalColumnMapping instance,
                final PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<?, ?> b) {
            b.decimalPrecision(instance.decimalPrecision);
            b.decimalScale(instance.decimalScale);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @return {@code this}.
         */
        public B decimalPrecision(final int decimalPrecision) {
            this.decimalPrecision = decimalPrecision;
            return self();
        }

        /**
         * @return {@code this}.
         */
        public B decimalScale(final int decimalScale) {
            this.decimalScale = decimalScale;
            return self();
        }

        @Override
        public java.lang.String toString() {
            return "PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder(super=" + super.toString()
                    + ", decimalPrecision=" + this.decimalPrecision + ", decimalScale=" + this.decimalScale + ")";
        }
    }

    private static final class PropertyToDecimalColumnMappingBuilderImpl extends
            PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<PropertyToDecimalColumnMapping, PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilderImpl> {

        private PropertyToDecimalColumnMappingBuilderImpl() {
        }

        @Override
        protected PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilderImpl self() {
            return this;
        }

        @Override
        public PropertyToDecimalColumnMapping build() {
            return new PropertyToDecimalColumnMapping(this);
        }
    }

    protected PropertyToDecimalColumnMapping(
            final PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<?, ?> b) {
        super(b);
        this.decimalPrecision = b.decimalPrecision;
        this.decimalScale = b.decimalScale;
    }

    public static PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<?, ?> builder() {
        return new PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilderImpl();
    }

    private PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilderImpl().fillValuesFrom(this);
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
