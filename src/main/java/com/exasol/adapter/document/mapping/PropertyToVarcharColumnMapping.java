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

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class PropertyToVarcharColumnMappingBuilder<C extends PropertyToVarcharColumnMapping, B extends PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private int varcharColumnSize;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private TruncateableMappingErrorBehaviour overflowBehaviour;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private ConvertableMappingErrorBehaviour nonStringBehaviour;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder
                    .$fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final PropertyToVarcharColumnMapping instance,
                final PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder<?, ?> b) {
            b.varcharColumnSize(instance.varcharColumnSize);
            b.overflowBehaviour(instance.overflowBehaviour);
            b.nonStringBehaviour(instance.nonStringBehaviour);
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
        public B overflowBehaviour(final TruncateableMappingErrorBehaviour overflowBehaviour) {
            this.overflowBehaviour = overflowBehaviour;
            return self();
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public B nonStringBehaviour(final ConvertableMappingErrorBehaviour nonStringBehaviour) {
            this.nonStringBehaviour = nonStringBehaviour;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder(super=" + super.toString()
                    + ", varcharColumnSize=" + this.varcharColumnSize + ", overflowBehaviour=" + this.overflowBehaviour
                    + ", nonStringBehaviour=" + this.nonStringBehaviour + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    private static final class PropertyToVarcharColumnMappingBuilderImpl extends
            PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder<PropertyToVarcharColumnMapping, PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilderImpl> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private PropertyToVarcharColumnMappingBuilderImpl() {
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilderImpl self() {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public PropertyToVarcharColumnMapping build() {
            return new PropertyToVarcharColumnMapping(this);
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected PropertyToVarcharColumnMapping(
            final PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder<?, ?> b) {
        super(b);
        this.varcharColumnSize = b.varcharColumnSize;
        this.overflowBehaviour = b.overflowBehaviour;
        this.nonStringBehaviour = b.nonStringBehaviour;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder<?, ?> builder() {
        return new PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilderImpl();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToVarcharColumnMapping.PropertyToVarcharColumnMappingBuilderImpl().$fillValuesFrom(this);
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
