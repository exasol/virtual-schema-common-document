package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;

/**
 * Abstract base for {@link AbstractPropertyToColumnMapping}s that map to numeric Exasol columns.
 */
public abstract class AbstractPropertyToNumberColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = -3412527315242611386L;
    /**
     * @serial
     */
    private final MappingErrorBehaviour overflowBehaviour;
    /**
     * @serial
     */
    private final ConvertableMappingErrorBehaviour notNumericBehaviour;

    MappingErrorBehaviour getOverflowBehaviour() {
        return overflowBehaviour;
    }

    ConvertableMappingErrorBehaviour getNotNumericBehaviour() {
        return notNumericBehaviour;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class AbstractPropertyToNumberColumnMappingBuilder<C extends AbstractPropertyToNumberColumnMapping, B extends AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private MappingErrorBehaviour overflowBehaviour;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private ConvertableMappingErrorBehaviour notNumericBehaviour;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder
                    .$fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final AbstractPropertyToNumberColumnMapping instance,
                final AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder<?, ?> b) {
            b.overflowBehaviour(instance.overflowBehaviour);
            b.notNumericBehaviour(instance.notNumericBehaviour);
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
        public B overflowBehaviour(final MappingErrorBehaviour overflowBehaviour) {
            this.overflowBehaviour = overflowBehaviour;
            return self();
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public B notNumericBehaviour(final ConvertableMappingErrorBehaviour notNumericBehaviour) {
            this.notNumericBehaviour = notNumericBehaviour;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder(super="
                    + super.toString() + ", overflowBehaviour=" + this.overflowBehaviour + ", notNumericBehaviour="
                    + this.notNumericBehaviour + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected AbstractPropertyToNumberColumnMapping(
            final AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder<?, ?> b) {
        super(b);
        this.overflowBehaviour = b.overflowBehaviour;
        this.notNumericBehaviour = b.notNumericBehaviour;
    }

    @Override
    public String toString() {
        return "AbstractPropertyToNumberColumnMapping(super=" + super.toString() + ", overflowBehaviour="
                + this.getOverflowBehaviour() + ", notNumericBehaviour=" + this.getNotNumericBehaviour() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(overflowBehaviour, notNumericBehaviour);
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
        final AbstractPropertyToNumberColumnMapping other = (AbstractPropertyToNumberColumnMapping) obj;
        return overflowBehaviour == other.overflowBehaviour && notNumericBehaviour == other.notNumericBehaviour;
    }
}
