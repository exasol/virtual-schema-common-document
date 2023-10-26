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

    /**
     * Builder for {@link PropertyToDecimalColumnMapping}.
     */
    public abstract static class Builder<C extends PropertyToDecimalColumnMapping, B extends PropertyToDecimalColumnMapping.Builder<C, B>>
            extends AbstractPropertyToNumberColumnMapping.Builder<C, B> {
        private int decimalPrecision;
        private int decimalScale;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToDecimalColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToDecimalColumnMapping instance,
                final PropertyToDecimalColumnMapping.Builder<?, ?> builder) {
            builder.decimalPrecision(instance.decimalPrecision);
            builder.decimalScale(instance.decimalScale);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @param decimalPrecision decimal precision
         * @return {@code this}.
         */
        public B decimalPrecision(final int decimalPrecision) {
            this.decimalPrecision = decimalPrecision;
            return self();
        }

        /**
         * @param decimalScale decimal scale
         * @return {@code this}.
         */
        public B decimalScale(final int decimalScale) {
            this.decimalScale = decimalScale;
            return self();
        }

        @Override
        public String toString() {
            return "PropertyToDecimalColumnMapping.PropertyToDecimalColumnMappingBuilder(super=" + super.toString()
                    + ", decimalPrecision=" + this.decimalPrecision + ", decimalScale=" + this.decimalScale + ")";
        }
    }

    static final class BuilderImpl extends
            PropertyToDecimalColumnMapping.Builder<PropertyToDecimalColumnMapping, PropertyToDecimalColumnMapping.BuilderImpl> {

        private BuilderImpl() {
        }

        @Override
        protected PropertyToDecimalColumnMapping.BuilderImpl self() {
            return this;
        }

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        @Override
        public PropertyToDecimalColumnMapping build() {
            return new PropertyToDecimalColumnMapping(this);
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected PropertyToDecimalColumnMapping(final PropertyToDecimalColumnMapping.Builder<?, ?> builder) {
        super(builder);
        this.decimalPrecision = builder.decimalPrecision;
        this.decimalScale = builder.decimalScale;
    }

    /**
     * Create a new builder for {@link PropertyToDecimalColumnMapping}.
     * 
     * @return a new builder
     */
    @SuppressWarnings("java:S1452") // Generic wildcard type is ok here
    public static PropertyToDecimalColumnMapping.Builder<?, ?> builder() {
        return new PropertyToDecimalColumnMapping.BuilderImpl();
    }

    private PropertyToDecimalColumnMapping.Builder<?, ?> toBuilder() {
        return new PropertyToDecimalColumnMapping.BuilderImpl().fillValuesFrom(this);
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
