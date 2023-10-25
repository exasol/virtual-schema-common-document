package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

/**
 * Maps a property of a DynamoDB table and all its descendants to a JSON string.
 */
public final class PropertyToJsonColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -6633690614095755071L;
    /** @serial */
    private final int varcharColumnSize;
    /** @serial */
    private final MappingErrorBehaviour overflowBehaviour;

    int getVarcharColumnSize() {
        return varcharColumnSize;
    }

    MappingErrorBehaviour getOverflowBehaviour() {
        return overflowBehaviour;
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return toBuilder().exasolColumnName(newExasolName).build();
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createVarChar(this.varcharColumnSize, DataType.ExaCharset.UTF8);
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class PropertyToJsonColumnMappingBuilder<C extends PropertyToJsonColumnMapping, B extends PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private int varcharColumnSize;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private MappingErrorBehaviour overflowBehaviour;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder.$fillValuesFromInstanceIntoBuilder(instance,
                    this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final PropertyToJsonColumnMapping instance,
                final PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder<?, ?> b) {
            b.varcharColumnSize(instance.varcharColumnSize);
            b.overflowBehaviour(instance.overflowBehaviour);
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
        public B varcharColumnSize(final int varcharColumnSize) {
            this.varcharColumnSize = varcharColumnSize;
            return self();
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public B overflowBehaviour(final MappingErrorBehaviour overflowBehaviour) {
            this.overflowBehaviour = overflowBehaviour;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder(super=" + super.toString()
                    + ", varcharColumnSize=" + this.varcharColumnSize + ", overflowBehaviour=" + this.overflowBehaviour
                    + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    private static final class PropertyToJsonColumnMappingBuilderImpl extends
            PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder<PropertyToJsonColumnMapping, PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilderImpl> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private PropertyToJsonColumnMappingBuilderImpl() {
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilderImpl self() {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public PropertyToJsonColumnMapping build() {
            return new PropertyToJsonColumnMapping(this);
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected PropertyToJsonColumnMapping(
            final PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder<?, ?> b) {
        super(b);
        this.varcharColumnSize = b.varcharColumnSize;
        this.overflowBehaviour = b.overflowBehaviour;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder<?, ?> builder() {
        return new PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilderImpl();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilderImpl().$fillValuesFrom(this);
    }

    @Override
    public String toString() {
        return "PropertyToJsonColumnMapping(super=" + super.toString() + ", varcharColumnSize="
                + this.getVarcharColumnSize() + ", overflowBehaviour=" + this.getOverflowBehaviour() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(varcharColumnSize, overflowBehaviour);
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
        final PropertyToJsonColumnMapping other = (PropertyToJsonColumnMapping) obj;
        return varcharColumnSize == other.varcharColumnSize && overflowBehaviour == other.overflowBehaviour;
    }
}
