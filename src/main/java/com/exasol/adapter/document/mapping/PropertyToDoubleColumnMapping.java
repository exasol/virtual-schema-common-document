package com.exasol.adapter.document.mapping;

import com.exasol.adapter.metadata.DataType;

/**
 * This class defines a mapping that extracts a floating-point number from the remote document and maps it to an Exasol
 * {@code DOUBLE-PRECISION} column.
 */
public final class PropertyToDoubleColumnMapping extends AbstractPropertyToNumberColumnMapping {
    private static final long serialVersionUID = 6021806680404016343L;

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createDouble();
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    /**
     * Builder for {@link PropertyToDoubleColumnMapping}.
     */
    public abstract static class PropertyToDoubleColumnMappingBuilder<C extends PropertyToDoubleColumnMapping, B extends PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilder<C, B>>
            extends AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder<C, B> {

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        @Override
        public String toString() {
            return "PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilder(super=" + super.toString() + ")";
        }
    }

    private static final class PropertyToDoubleColumnMappingBuilderImpl extends
            PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilder<PropertyToDoubleColumnMapping, PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilderImpl> {

        private PropertyToDoubleColumnMappingBuilderImpl() {
        }

        @Override
        protected PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilderImpl self() {
            return this;
        }

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        @Override
        public PropertyToDoubleColumnMapping build() {
            return new PropertyToDoubleColumnMapping(this);
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected PropertyToDoubleColumnMapping(
            final PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilder<?, ?> builder) {
        super(builder);
    }

    /**
     * Create a new builder for {@link PropertyToDoubleColumnMapping}.
     * 
     * @return a new builder
     */
    public static PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilder<?, ?> builder() {
        return new PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilderImpl();
    }

    private PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToDoubleColumnMapping.PropertyToDoubleColumnMappingBuilderImpl().fillValuesFrom(this);
    }

    @Override
    public String toString() {
        return "PropertyToDoubleColumnMapping(super=" + super.toString() + ")";
    }

    @Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PropertyToDoubleColumnMapping)) {
            return false;
        }
        if (!(this instanceof PropertyToDoubleColumnMapping)) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
