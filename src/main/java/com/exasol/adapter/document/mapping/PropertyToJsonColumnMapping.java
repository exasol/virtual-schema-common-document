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

    /**
     * Builder for {@link PropertyToJsonColumnMapping}.
     */
    public abstract static class Builder<C extends PropertyToJsonColumnMapping, B extends PropertyToJsonColumnMapping.Builder<C, B>>
            extends AbstractPropertyToColumnMapping.Builder<C, B> {
        private int varcharColumnSize;
        private MappingErrorBehaviour overflowBehaviour;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToJsonColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToJsonColumnMapping instance,
                final PropertyToJsonColumnMapping.Builder<?, ?> builder) {
            builder.varcharColumnSize(instance.varcharColumnSize);
            builder.overflowBehaviour(instance.overflowBehaviour);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @param varcharColumnSize varchar column size
         * @return {@code this}.
         */
        public B varcharColumnSize(final int varcharColumnSize) {
            this.varcharColumnSize = varcharColumnSize;
            return self();
        }

        /**
         * @param overflowBehaviour overflow behaviour
         * @return {@code this}.
         */
        public B overflowBehaviour(final MappingErrorBehaviour overflowBehaviour) {
            this.overflowBehaviour = overflowBehaviour;
            return self();
        }

        @Override
        public String toString() {
            return "PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilder(super=" + super.toString()
                    + ", varcharColumnSize=" + this.varcharColumnSize + ", overflowBehaviour=" + this.overflowBehaviour
                    + ")";
        }
    }

    private static final class PropertyToJsonColumnMappingBuilderImpl extends
            PropertyToJsonColumnMapping.Builder<PropertyToJsonColumnMapping, PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilderImpl> {

        private PropertyToJsonColumnMappingBuilderImpl() {
        }

        @Override
        protected PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilderImpl self() {
            return this;
        }

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        @Override
        public PropertyToJsonColumnMapping build() {
            return new PropertyToJsonColumnMapping(this);
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected PropertyToJsonColumnMapping(final PropertyToJsonColumnMapping.Builder<?, ?> builder) {
        super(builder);
        this.varcharColumnSize = builder.varcharColumnSize;
        this.overflowBehaviour = builder.overflowBehaviour;
    }

    /**
     * Create a new builder for {@link PropertyToJsonColumnMapping}.
     * 
     * @return a new builder
     */
    @SuppressWarnings("java:S1452") // Generic wildcard type is ok here
    public static PropertyToJsonColumnMapping.Builder<?, ?> builder() {
        return new PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilderImpl();
    }

    private PropertyToJsonColumnMapping.Builder<?, ?> toBuilder() {
        return new PropertyToJsonColumnMapping.PropertyToJsonColumnMappingBuilderImpl().fillValuesFrom(this);
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
