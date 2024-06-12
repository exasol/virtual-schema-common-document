package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

/**
 * This class defines a mapping that extracts a timestamp value from the remote document and maps it to an Exasol
 * {@code TIMESTAMP} column.
 */
public final class PropertyToTimestampColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 2336854835413425712L;
    private static final int DEFAULT_TIMESTAMP_PRECISION = 6;
    /** @serial */
    private final ConvertableMappingErrorBehaviour notTimestampBehaviour;

    ConvertableMappingErrorBehaviour getNotTimestampBehaviour() {
        return notTimestampBehaviour;
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createTimestamp(false, DEFAULT_TIMESTAMP_PRECISION);
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

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            PropertyToTimestampColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final PropertyToTimestampColumnMapping instance,
                final PropertyToTimestampColumnMapping.Builder<?, ?> builder) {
            builder.notTimestampBehaviour(instance.notTimestampBehaviour);
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

        @Override
        public String toString() {
            return "PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder(super=" + super.toString()
                    + ", notTimestampBehaviour=" + this.notTimestampBehaviour + ")";
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
                + this.getNotTimestampBehaviour() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + Objects.hash(notTimestampBehaviour);
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
        return notTimestampBehaviour == other.notTimestampBehaviour;
    }
}
