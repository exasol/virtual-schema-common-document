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

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class PropertyToDecimalColumnMappingBuilder<C extends PropertyToDecimalColumnMapping, B extends PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<C, B>>
            extends AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private int decimalPrecision;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private int decimalScale;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder
                    .$fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final PropertyToDecimalColumnMapping instance,
                final PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<?, ?> b) {
            b.decimalPrecision(instance.decimalPrecision);
            b.decimalScale(instance.decimalScale);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected abstract B self();

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public abstract C build();

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public B decimalPrecision(final int decimalPrecision) {
            this.decimalPrecision = decimalPrecision;
            return self();
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public B decimalScale(final int decimalScale) {
            this.decimalScale = decimalScale;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder(super=" + super.toString()
                    + ", decimalPrecision=" + this.decimalPrecision + ", decimalScale=" + this.decimalScale + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    private static final class PropertyToDecimalColumnMappingBuilderImpl extends
            PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<PropertyToDecimalColumnMapping, PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilderImpl> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private PropertyToDecimalColumnMappingBuilderImpl() {
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilderImpl self() {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public PropertyToDecimalColumnMapping build() {
            return new PropertyToDecimalColumnMapping(this);
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected PropertyToDecimalColumnMapping(
            final PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<?, ?> b) {
        super(b);
        this.decimalPrecision = b.decimalPrecision;
        this.decimalScale = b.decimalScale;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<?, ?> builder() {
        return new PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilderImpl();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilderImpl().$fillValuesFrom(this);
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
