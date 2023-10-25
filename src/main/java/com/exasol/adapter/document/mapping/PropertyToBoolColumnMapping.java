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

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class PropertyToBoolColumnMappingBuilder<C extends PropertyToBoolColumnMapping, B extends PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private ConvertableMappingErrorBehaviour notBooleanBehavior;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder.$fillValuesFromInstanceIntoBuilder(instance,
                    this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final PropertyToBoolColumnMapping instance,
                final PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> b) {
            b.notBooleanBehavior(instance.notBooleanBehavior);
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
        public B notBooleanBehavior(final ConvertableMappingErrorBehaviour notBooleanBehavior) {
            this.notBooleanBehavior = notBooleanBehavior;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder(super=" + super.toString()
                    + ", notBooleanBehavior=" + this.notBooleanBehavior + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    private static final class PropertyToBoolColumnMappingBuilderImpl extends
            PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<PropertyToBoolColumnMapping, PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilderImpl> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private PropertyToBoolColumnMappingBuilderImpl() {
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilderImpl self() {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public PropertyToBoolColumnMapping build() {
            return new PropertyToBoolColumnMapping(this);
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected PropertyToBoolColumnMapping(
            final PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> b) {
        super(b);
        this.notBooleanBehavior = b.notBooleanBehavior;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> builder() {
        return new PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilderImpl();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilderImpl().$fillValuesFrom(this);
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
