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

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class AbstractColumnMappingBuilder<C extends AbstractColumnMapping, B extends AbstractColumnMapping.AbstractColumnMappingBuilder<C, B>> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private String exasolColumnName;

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            AbstractColumnMapping.AbstractColumnMappingBuilder.$fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final AbstractColumnMapping instance,
                final AbstractColumnMapping.AbstractColumnMappingBuilder<?, ?> b) {
            b.exasolColumnName(instance.exasolColumnName);
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected abstract B self();

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public abstract C build();

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public B exasolColumnName(final String exasolColumnName) {
            this.exasolColumnName = exasolColumnName;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "AbstractColumnMapping.AbstractColumnMappingBuilder(exasolColumnName=" + this.exasolColumnName + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected AbstractColumnMapping(final AbstractColumnMapping.AbstractColumnMappingBuilder<?, ?> b) {
        this.exasolColumnName = b.exasolColumnName;
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
