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

    public abstract static class PropertyToBoolColumnMappingBuilder<C extends PropertyToBoolColumnMapping, B extends PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {
        private ConvertableMappingErrorBehaviour notBooleanBehavior;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder.fillValuesFromInstanceIntoBuilder(instance,
                    this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToBoolColumnMapping instance,
                final PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> b) {
            b.notBooleanBehavior(instance.notBooleanBehavior);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @return {@code this}.
         */
        public B notBooleanBehavior(final ConvertableMappingErrorBehaviour notBooleanBehavior) {
            this.notBooleanBehavior = notBooleanBehavior;
            return self();
        }

        @Override
        public java.lang.String toString() {
            return "PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder(super=" + super.toString()
                    + ", notBooleanBehavior=" + this.notBooleanBehavior + ")";
        }
    }

    private static final class PropertyToBoolColumnMappingBuilderImpl extends
            PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<PropertyToBoolColumnMapping, PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilderImpl> {

        private PropertyToBoolColumnMappingBuilderImpl() {
        }

        @Override
        protected PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilderImpl self() {
            return this;
        }

        @Override
        public PropertyToBoolColumnMapping build() {
            return new PropertyToBoolColumnMapping(this);
        }
    }

    protected PropertyToBoolColumnMapping(
            final PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> b) {
        super(b);
        this.notBooleanBehavior = b.notBooleanBehavior;
    }

    public static PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> builder() {
        return new PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilderImpl();
    }

    private PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilderImpl().fillValuesFrom(this);
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
