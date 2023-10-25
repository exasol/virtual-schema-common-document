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

    public static abstract class PropertyToTimestampColumnMappingBuilder<C extends PropertyToTimestampColumnMapping, B extends PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {

        private ConvertableMappingErrorBehaviour notTimestampBehaviour;

        private boolean useTimestampWithLocalTimezoneType;

        @Override

        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder
                    .$fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void $fillValuesFromInstanceIntoBuilder(final PropertyToTimestampColumnMapping instance,
                final PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<?, ?> b) {
            b.notTimestampBehaviour(instance.notTimestampBehaviour);
            b.useTimestampWithLocalTimezoneType(instance.useTimestampWithLocalTimezoneType);
        }

        @Override

        protected abstract B self();

        @Override

        public abstract C build();

        /**
         * @return {@code this}.
         */

        public B notTimestampBehaviour(final ConvertableMappingErrorBehaviour notTimestampBehaviour) {
            this.notTimestampBehaviour = notTimestampBehaviour;
            return self();
        }

        /**
         * @return {@code this}.
         */

        public B useTimestampWithLocalTimezoneType(final boolean useTimestampWithLocalTimezoneType) {
            this.useTimestampWithLocalTimezoneType = useTimestampWithLocalTimezoneType;
            return self();
        }

        @Override

        public java.lang.String toString() {
            return "PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder(super=" + super.toString()
                    + ", notTimestampBehaviour=" + this.notTimestampBehaviour + ", useTimestampWithLocalTimezoneType="
                    + this.useTimestampWithLocalTimezoneType + ")";
        }
    }

    private static final class PropertyToTimestampColumnMappingBuilderImpl extends
            PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<PropertyToTimestampColumnMapping, PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilderImpl> {

        private PropertyToTimestampColumnMappingBuilderImpl() {
        }

        @Override

        protected PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilderImpl self() {
            return this;
        }

        @Override

        public PropertyToTimestampColumnMapping build() {
            return new PropertyToTimestampColumnMapping(this);
        }
    }

    protected PropertyToTimestampColumnMapping(
            final PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<?, ?> b) {
        super(b);
        this.notTimestampBehaviour = b.notTimestampBehaviour;
        this.useTimestampWithLocalTimezoneType = b.useTimestampWithLocalTimezoneType;
    }

    public static PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilder<?, ?> builder() {
        return new PropertyToTimestampColumnMapping.PropertyToTimestampColumnMappingBuilderImpl();
    }

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
