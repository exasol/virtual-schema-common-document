package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

/**
 * This class defines a mapping that extracts a boolean value from the remote document and maps it to an Exasol
 * {@code BOOLEAN} column.
 */
public final class PropertyToBoolColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 7665762375515945443L;
    /** @serial */
    private final ConvertableMappingErrorBehaviour notBooleanBehavior;

    ConvertableMappingErrorBehaviour getNotBooleanBehavior() {
        return notBooleanBehavior;
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createBool();
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    /**
     * Builder for {@link PropertyToBoolColumnMapping}.
     */
    public abstract static class Builder<C extends PropertyToBoolColumnMapping, B extends PropertyToBoolColumnMapping.Builder<C, B>>
            extends AbstractPropertyToColumnMapping.Builder<C, B> {
        private ConvertableMappingErrorBehaviour notBooleanBehavior;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToBoolColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToBoolColumnMapping instance,
                final PropertyToBoolColumnMapping.Builder<?, ?> builder) {
            builder.notBooleanBehavior(instance.notBooleanBehavior);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @param notBooleanBehavior behaviour in case value is not a boolean
         * @return {@code this}.
         */
        public B notBooleanBehavior(final ConvertableMappingErrorBehaviour notBooleanBehavior) {
            this.notBooleanBehavior = notBooleanBehavior;
            return self();
        }

        @Override
        public String toString() {
            return "PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder(super=" + super.toString()
                    + ", notBooleanBehavior=" + this.notBooleanBehavior + ")";
        }
    }

    private static final class BuilderImpl extends
            PropertyToBoolColumnMapping.Builder<PropertyToBoolColumnMapping, PropertyToBoolColumnMapping.BuilderImpl> {

        private BuilderImpl() {
        }

        @Override
        protected PropertyToBoolColumnMapping.BuilderImpl self() {
            return this;
        }

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        @Override
        public PropertyToBoolColumnMapping build() {
            return new PropertyToBoolColumnMapping(this);
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected PropertyToBoolColumnMapping(final PropertyToBoolColumnMapping.Builder<?, ?> builder) {
        super(builder);
        this.notBooleanBehavior = builder.notBooleanBehavior;
    }

    /**
     * Create a new builder for {@link PropertyToBoolColumnMapping}.
     * 
     * @return a new builder
     */
    @SuppressWarnings("java:S1452") // Generic wildcard type is ok here
    public static PropertyToBoolColumnMapping.Builder<?, ?> builder() {
        return new PropertyToBoolColumnMapping.BuilderImpl();
    }

    private PropertyToBoolColumnMapping.Builder<?, ?> toBuilder() {
        return new PropertyToBoolColumnMapping.BuilderImpl().fillValuesFrom(this);
    }

    @Override
    public String toString() {
        return "PropertyToBoolColumnMapping(super=" + super.toString() + ", notBooleanBehavior="
                + this.getNotBooleanBehavior() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(notBooleanBehavior);
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
        final PropertyToBoolColumnMapping other = (PropertyToBoolColumnMapping) obj;
        return notBooleanBehavior == other.notBooleanBehavior;
    }
}
