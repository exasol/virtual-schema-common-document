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

    /**
     * Abstract builder for {@link AbstractPropertyToNumberColumnMapping}.
     */
    public abstract static class Builder<C extends AbstractPropertyToNumberColumnMapping, B extends AbstractPropertyToNumberColumnMapping.Builder<C, B>>
            extends AbstractPropertyToColumnMapping.Builder<C, B> {
        private MappingErrorBehaviour overflowBehaviour;
        private ConvertableMappingErrorBehaviour notNumericBehaviour;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            AbstractPropertyToNumberColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final AbstractPropertyToNumberColumnMapping instance,
                final AbstractPropertyToNumberColumnMapping.Builder<?, ?> builder) {
            builder.overflowBehaviour(instance.overflowBehaviour);
            builder.notNumericBehaviour(instance.notNumericBehaviour);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @param overflowBehaviour behaviour in case of overflow
         * @return {@code this}.
         */
        public B overflowBehaviour(final MappingErrorBehaviour overflowBehaviour) {
            this.overflowBehaviour = overflowBehaviour;
            return self();
        }

        /**
         * @param notNumericBehaviour behaviour in case the value is not numeric
         * @return {@code this}.
         */
        public B notNumericBehaviour(final ConvertableMappingErrorBehaviour notNumericBehaviour) {
            this.notNumericBehaviour = notNumericBehaviour;
            return self();
        }

        @Override
        public String toString() {
            return "AbstractPropertyToNumberColumnMapping.AbstractPropertyToNumberColumnMappingBuilder(super="
                    + super.toString() + ", overflowBehaviour=" + this.overflowBehaviour + ", notNumericBehaviour="
                    + this.notNumericBehaviour + ")";
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected AbstractPropertyToNumberColumnMapping(final AbstractPropertyToNumberColumnMapping.Builder<?, ?> builder) {
        super(builder);
        this.overflowBehaviour = builder.overflowBehaviour;
        this.notNumericBehaviour = builder.notNumericBehaviour;
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
