package com.exasol.adapter.document.mapping;

import java.util.Objects;

/**
 * This class is an abstract basis for {@link ColumnMapping}s.
 */
abstract class AbstractColumnMapping implements ColumnMapping {
    private static final long serialVersionUID = -3284843747319182683L;
    private final String exasolColumnName;

    @Override
    public String getExasolColumnName() {
        return exasolColumnName;
    }

    /**
     * Abstract builder for {@link AbstractColumnMapping}.
     */
    public abstract static class Builder<C extends AbstractColumnMapping, B extends AbstractColumnMapping.Builder<C, B>> {
        private String exasolColumnName;

        /**
         * Copy values from the given instance into this builder.
         * 
         * @param instance instance from which to copy
         * @return {@code this}
         */
        protected B fillValuesFrom(final C instance) {
            AbstractColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final AbstractColumnMapping instance,
                final AbstractColumnMapping.Builder<?, ?> builder) {
            builder.exasolColumnName(instance.exasolColumnName);
        }

        /**
         * Get this builder instance.
         * 
         * @return this builder instance.
         */
        protected abstract B self();

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        public abstract C build();

        /**
         * @return {@code this}.
         */
        public B exasolColumnName(final String exasolColumnName) {
            this.exasolColumnName = exasolColumnName;
            return self();
        }

        @Override
        public String toString() {
            return "AbstractColumnMapping.AbstractColumnMappingBuilder(exasolColumnName=" + this.exasolColumnName + ")";
        }
    }

    /**
     * Create a new instance from a builder.
     * 
     * @param builder builder
     */
    protected AbstractColumnMapping(final AbstractColumnMapping.Builder<?, ?> builder) {
        this.exasolColumnName = builder.exasolColumnName;
    }

    @Override
    public String toString() {
        return "AbstractColumnMapping(exasolColumnName=" + this.getExasolColumnName() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(exasolColumnName);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractColumnMapping other = (AbstractColumnMapping) obj;
        return Objects.equals(exasolColumnName, other.exasolColumnName);
    }
}
