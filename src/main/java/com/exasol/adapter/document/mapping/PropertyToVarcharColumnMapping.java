package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.edml.TruncateableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

/**
 * This class defines a mapping that extracts a string from the remote document and maps it to an Exasol VARCHAR column.
 */
public final class PropertyToVarcharColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 331013763747038031L;
    /** @serial */
    private final int varcharColumnSize;
    /** @serial */
    private final TruncateableMappingErrorBehaviour overflowBehaviour;
    /** @serial */
    private final ConvertableMappingErrorBehaviour nonStringBehaviour;

    /**
     * Get the column size.
     * 
     * @return column size
     */
    public int getVarcharColumnSize() {
        return varcharColumnSize;
    }

    /**
     * Get the overflow behaviour.
     * 
     * @return overflow behaviour
     */
    public TruncateableMappingErrorBehaviour getOverflowBehaviour() {
        return overflowBehaviour;
    }

    /**
     * Get the non-string behaviour.
     * 
     * @return non-string behaviour
     */
    public ConvertableMappingErrorBehaviour getNonStringBehaviour() {
        return nonStringBehaviour;
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createVarChar(this.varcharColumnSize, DataType.ExaCharset.UTF8);
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    /**
     * Builder for {@link PropertyToVarcharColumnMapping}.
     */
    public abstract static class Builder<C extends PropertyToVarcharColumnMapping, B extends PropertyToVarcharColumnMapping.Builder<C, B>>
            extends AbstractPropertyToColumnMapping.Builder<C, B> {
        private int varcharColumnSize;
        private TruncateableMappingErrorBehaviour overflowBehaviour;
        private ConvertableMappingErrorBehaviour nonStringBehaviour;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToVarcharColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToVarcharColumnMapping instance,
                final PropertyToVarcharColumnMapping.Builder<?, ?> builder) {
            builder.varcharColumnSize(instance.varcharColumnSize);
            builder.overflowBehaviour(instance.overflowBehaviour);
            builder.nonStringBehaviour(instance.nonStringBehaviour);
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
        public B overflowBehaviour(final TruncateableMappingErrorBehaviour overflowBehaviour) {
            this.overflowBehaviour = overflowBehaviour;
            return self();
        }

        /**
         * @param nonStringBehaviour non-string behaviour
         * @return {@code this}.
         */
        public B nonStringBehaviour(final ConvertableMappingErrorBehaviour nonStringBehaviour) {
            this.nonStringBehaviour = nonStringBehaviour;
            return self();
        }

        @Override
        public String toString() {
            return "PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder(super=" + super.toString()
                    + ", varcharColumnSize=" + this.varcharColumnSize + ", overflowBehaviour=" + this.overflowBehaviour
                    + ", nonStringBehaviour=" + this.nonStringBehaviour + ")";
        }
    }

    private static final class BuilderImpl extends
            PropertyToVarcharColumnMapping.Builder<PropertyToVarcharColumnMapping, PropertyToVarcharColumnMapping.BuilderImpl> {

        private BuilderImpl() {
        }

        @Override
        protected PropertyToVarcharColumnMapping.BuilderImpl self() {
            return this;
        }

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        @Override
        public PropertyToVarcharColumnMapping build() {
            return new PropertyToVarcharColumnMapping(this);
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected PropertyToVarcharColumnMapping(final PropertyToVarcharColumnMapping.Builder<?, ?> builder) {
        super(builder);
        this.varcharColumnSize = builder.varcharColumnSize;
        this.overflowBehaviour = builder.overflowBehaviour;
        this.nonStringBehaviour = builder.nonStringBehaviour;
    }

    /**
     * Create a new builder for {@link PropertyToVarcharColumnMapping}.
     * 
     * @return a new builder
     */
    @SuppressWarnings("java:S1452") // Generic wildcard type is ok here
    public static PropertyToVarcharColumnMapping.Builder<?, ?> builder() {
        return new PropertyToVarcharColumnMapping.BuilderImpl();
    }

    @SuppressWarnings("java:S1452") // Generic wildcard type is ok here
    PropertyToVarcharColumnMapping.Builder<?, ?> toBuilder() {
        return new PropertyToVarcharColumnMapping.BuilderImpl().fillValuesFrom(this);
    }

    @Override
    public String toString() {
        return "PropertyToVarcharColumnMapping(super=" + super.toString() + ", varcharColumnSize="
                + this.getVarcharColumnSize() + ", overflowBehaviour=" + this.getOverflowBehaviour()
                + ", nonStringBehaviour=" + this.getNonStringBehaviour() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(varcharColumnSize, overflowBehaviour, nonStringBehaviour);
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
        final PropertyToVarcharColumnMapping other = (PropertyToVarcharColumnMapping) obj;
        return varcharColumnSize == other.varcharColumnSize && overflowBehaviour == other.overflowBehaviour
                && nonStringBehaviour == other.nonStringBehaviour;
    }
}
