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

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class PropertyToDateColumnMappingBuilder<C extends PropertyToDateColumnMapping, B extends PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private ConvertableMappingErrorBehaviour notDateBehaviour;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder.$fillValuesFromInstanceIntoBuilder(instance,
                    this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final PropertyToDateColumnMapping instance,
                final PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> b) {
            b.notDateBehaviour(instance.notDateBehaviour);
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
        public B notDateBehaviour(final ConvertableMappingErrorBehaviour notDateBehaviour) {
            this.notDateBehaviour = notDateBehaviour;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder(super=" + super.toString()
                    + ", notDateBehaviour=" + this.notDateBehaviour + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    private static final class PropertyToDateColumnMappingBuilderImpl extends
            PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<PropertyToDateColumnMapping, PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilderImpl> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private PropertyToDateColumnMappingBuilderImpl() {
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilderImpl self() {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public PropertyToDateColumnMapping build() {
            return new PropertyToDateColumnMapping(this);
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected PropertyToDateColumnMapping(
            final PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> b) {
        super(b);
        this.notDateBehaviour = b.notDateBehaviour;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> builder() {
        return new PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilderImpl();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilderImpl().$fillValuesFrom(this);
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
