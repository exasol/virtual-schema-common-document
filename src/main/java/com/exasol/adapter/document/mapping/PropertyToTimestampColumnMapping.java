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

    /**
     * Builder for {@link PropertyToTimestampColumnMapping}.
     */
    public abstract static class Builder<C extends PropertyToTimestampColumnMapping, B extends PropertyToTimestampColumnMapping.Builder<C, B>>
            extends AbstractPropertyToColumnMapping.Builder<C, B> {
        private ConvertableMappingErrorBehaviour notTimestampBehaviour;
        private boolean useTimestampWithLocalTimezoneType;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToTimestampColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToTimestampColumnMapping instance,
                final PropertyToTimestampColumnMapping.Builder<?, ?> builder) {
            builder.notTimestampBehaviour(instance.notTimestampBehaviour);
            builder.useTimestampWithLocalTimezoneType(instance.useTimestampWithLocalTimezoneType);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @param notTimestampBehaviour behaviour in case a value is not a timestamp
         * @return {@code this}.
         */
        public B notTimestampBehaviour(final ConvertableMappingErrorBehaviour notTimestampBehaviour) {
            this.notTimestampBehaviour = notTimestampBehaviour;
            return self();
        }

        /**
         * @param useTimestampWithLocalTimezoneType {@code true} if timestamps should use {@code LOCAL TIMEZONE}
         * @return {@code this}.
         */
        public B useTimestampWithLocalTimezoneType(final boolean useTimestampWithLocalTimezoneType) {
            this.useTimestampWithLocalTimezoneType = useTimestampWithLocalTimezoneType;
            return self();
        }

        @Override
        public String toString() {
            return "PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder(super=" + super.toString()
                    + ", notTimestampBehaviour=" + this.notTimestampBehaviour + ", useTimestampWithLocalTimezoneType="
                    + this.useTimestampWithLocalTimezoneType + ")";
        }
    }

    static final class BuilderImpl extends
            PropertyToTimestampColumnMapping.Builder<PropertyToTimestampColumnMapping, PropertyToTimestampColumnMapping.BuilderImpl> {

        private BuilderImpl() {
        }

        @Override
        protected PropertyToTimestampColumnMapping.BuilderImpl self() {
            return this;
        }

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        @Override
        public PropertyToTimestampColumnMapping build() {
            return new PropertyToTimestampColumnMapping(this);
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected PropertyToTimestampColumnMapping(final PropertyToTimestampColumnMapping.Builder<?, ?> builder) {
        super(builder);
        this.notTimestampBehaviour = builder.notTimestampBehaviour;
        this.useTimestampWithLocalTimezoneType = builder.useTimestampWithLocalTimezoneType;
    }

    /**
     * Create a new builder for {@link PropertyToTimestampColumnMapping}.
     * 
     * @return a new builder
     */
    @SuppressWarnings("java:S1452") // Generic wildcard type is ok here
    public static PropertyToTimestampColumnMapping.Builder<?, ?> builder() {
        return new PropertyToTimestampColumnMapping.BuilderImpl();
    }

    @SuppressWarnings("java:S1452") // Generic wildcard type is ok here
    PropertyToTimestampColumnMapping.Builder<?, ?> toBuilder() {
        return new PropertyToTimestampColumnMapping.BuilderImpl().fillValuesFrom(this);
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
