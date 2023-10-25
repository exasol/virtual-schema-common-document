package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

/**
 * This class defines a mapping that extracts a date value from the remote document and maps it to an Exasol
 * {@code DATE} column.
 */
public final class PropertyToDateColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 6169627871770637281L;
    /** @serial */
    private final ConvertableMappingErrorBehaviour notDateBehaviour;

    ConvertableMappingErrorBehaviour getNotDateBehaviour() {
        return notDateBehaviour;
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createDate();
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    /**
     * Builder for {@link PropertyToDateColumnMapping}.
     */
    public abstract static class PropertyToDateColumnMappingBuilder<C extends PropertyToDateColumnMapping, B extends PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {
        private ConvertableMappingErrorBehaviour notDateBehaviour;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder.fillValuesFromInstanceIntoBuilder(instance,
                    this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToDateColumnMapping instance,
                final PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> b) {
            b.notDateBehaviour(instance.notDateBehaviour);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @param notDateBehaviour behaviour in case value is not a date
         * @return {@code this}.
         */
        public B notDateBehaviour(final ConvertableMappingErrorBehaviour notDateBehaviour) {
            this.notDateBehaviour = notDateBehaviour;
            return self();
        }

        @Override
        public String toString() {
            return "PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder(super=" + super.toString()
                    + ", notDateBehaviour=" + this.notDateBehaviour + ")";
        }
    }

    private static final class PropertyToDateColumnMappingBuilderImpl extends
            PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<PropertyToDateColumnMapping, PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilderImpl> {

        private PropertyToDateColumnMappingBuilderImpl() {
        }

        @Override
        protected PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilderImpl self() {
            return this;
        }

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        @Override
        public PropertyToDateColumnMapping build() {
            return new PropertyToDateColumnMapping(this);
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected PropertyToDateColumnMapping(
            final PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> builder) {
        super(builder);
        this.notDateBehaviour = builder.notDateBehaviour;
    }

    /**
     * Create a new builder for {@link PropertyToDateColumnMapping}.
     * 
     * @return a new builder
     */
    public static PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> builder() {
        return new PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilderImpl();
    }

    private PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilderImpl().fillValuesFrom(this);
    }

    @Override
    public String toString() {
        return "PropertyToDateColumnMapping(super=" + super.toString() + ", notDateBehaviour="
                + this.getNotDateBehaviour() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(notDateBehaviour);
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
        final PropertyToDateColumnMapping other = (PropertyToDateColumnMapping) obj;
        return notDateBehaviour == other.notDateBehaviour;
    }
}
