package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

/**
 * This class defines a mapping that extracts a timestamp value from the remote document and maps it to an Exasol
 * {@code TIMESTAMP} or {@code TIMESTAMP WITH LOCAL TIMEZONE} column.
 */
public final class PropertyToTimestampColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 2336854835413425711L;
    /** @serial */
    private final ConvertableMappingErrorBehaviour notTimestampBehaviour;
    /** @serial */
    private final boolean useTimestampWithLocalTimezoneType;

    ConvertableMappingErrorBehaviour getNotTimestampBehaviour() {
        return notTimestampBehaviour;
    }

    boolean isUseTimestampWithLocalTimezoneType() {
        return useTimestampWithLocalTimezoneType;
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createTimestamp(this.useTimestampWithLocalTimezoneType);
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class PropertyToTimestampColumnMappingBuilder<C extends PropertyToTimestampColumnMapping, B extends PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private ConvertableMappingErrorBehaviour notTimestampBehaviour;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private boolean useTimestampWithLocalTimezoneType;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder
                    .$fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final PropertyToTimestampColumnMapping instance,
                final PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<?, ?> b) {
            b.notTimestampBehaviour(instance.notTimestampBehaviour);
            b.useTimestampWithLocalTimezoneType(instance.useTimestampWithLocalTimezoneType);
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
        public B notTimestampBehaviour(final ConvertableMappingErrorBehaviour notTimestampBehaviour) {
            this.notTimestampBehaviour = notTimestampBehaviour;
            return self();
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public B useTimestampWithLocalTimezoneType(final boolean useTimestampWithLocalTimezoneType) {
            this.useTimestampWithLocalTimezoneType = useTimestampWithLocalTimezoneType;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder(super=" + super.toString()
                    + ", notTimestampBehaviour=" + this.notTimestampBehaviour + ", useTimestampWithLocalTimezoneType="
                    + this.useTimestampWithLocalTimezoneType + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    private static final class PropertyToTimestampColumnMappingBuilderImpl extends
            PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<PropertyToTimestampColumnMapping, PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilderImpl> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private PropertyToTimestampColumnMappingBuilderImpl() {
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilderImpl self() {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public PropertyToTimestampColumnMapping build() {
            return new PropertyToTimestampColumnMapping(this);
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected PropertyToTimestampColumnMapping(
            final PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<?, ?> b) {
        super(b);
        this.notTimestampBehaviour = b.notTimestampBehaviour;
        this.useTimestampWithLocalTimezoneType = b.useTimestampWithLocalTimezoneType;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<?, ?> builder() {
        return new PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilderImpl();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<?, ?> toBuilder() {
        return new PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilderImpl().$fillValuesFrom(this);
    }

    @Override
    public String toString() {
        return "PropertyToTimestampColumnMapping(super=" + super.toString() + ", notTimestampBehaviour="
                + this.getNotTimestampBehaviour() + ", useTimestampWithLocalTimezoneType="
                + this.isUseTimestampWithLocalTimezoneType() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(notTimestampBehaviour, useTimestampWithLocalTimezoneType);
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
        final PropertyToTimestampColumnMapping other = (PropertyToTimestampColumnMapping) obj;
        return notTimestampBehaviour == other.notTimestampBehaviour
                && useTimestampWithLocalTimezoneType == other.useTimestampWithLocalTimezoneType;
    }
}
