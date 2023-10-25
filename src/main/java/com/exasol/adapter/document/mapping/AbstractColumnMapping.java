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

    public static abstract class AbstractColumnMappingBuilder<C extends AbstractColumnMapping, B extends AbstractColumnMapping.AbstractColumnMappingBuilder<C, B>> {

        private String exasolColumnName;

        protected B $fillValuesFrom(final C instance) {
            AbstractColumnMapping.AbstractColumnMappingBuilder.$fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void $fillValuesFromInstanceIntoBuilder(final AbstractColumnMapping instance,
                final AbstractColumnMapping.AbstractColumnMappingBuilder<?, ?> b) {
            b.exasolColumnName(instance.exasolColumnName);
        }

        protected abstract B self();

        public abstract C build();

        /**
         * @return {@code this}.
         */

        public B exasolColumnName(final String exasolColumnName) {
            this.exasolColumnName = exasolColumnName;
            return self();
        }

        @Override

        public java.lang.String toString() {
            return "AbstractColumnMapping.AbstractColumnMappingBuilder(exasolColumnName=" + this.exasolColumnName + ")";
        }
    }

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
