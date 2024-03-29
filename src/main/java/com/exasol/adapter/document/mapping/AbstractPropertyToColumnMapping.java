package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;

/**
 * This class is an abstract basis for {@link PropertyToColumnMapping}s.
 */
abstract class AbstractPropertyToColumnMapping extends AbstractColumnMapping implements PropertyToColumnMapping {
    private static final long serialVersionUID = -5125991213244975414L;
    private final DocumentPathExpression pathToSourceProperty;
    private final MappingErrorBehaviour lookupFailBehaviour;

    @Override
    public DocumentPathExpression getPathToSourceProperty() {
        return pathToSourceProperty;
    }

    @Override
    public MappingErrorBehaviour getLookupFailBehaviour() {
        return lookupFailBehaviour;
    }

    @Override
    public boolean isExasolColumnNullable() {
        return true;
    }

    public abstract static class Builder<C extends AbstractPropertyToColumnMapping, B extends AbstractPropertyToColumnMapping.Builder<C, B>>
            extends AbstractColumnMapping.Builder<C, B> {
        private DocumentPathExpression pathToSourceProperty;
        private MappingErrorBehaviour lookupFailBehaviour;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            AbstractPropertyToColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final AbstractPropertyToColumnMapping instance,
                final AbstractPropertyToColumnMapping.Builder<?, ?> builder) {
            builder.pathToSourceProperty(instance.pathToSourceProperty);
            builder.lookupFailBehaviour(instance.lookupFailBehaviour);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @return {@code this}.
         */
        public B pathToSourceProperty(final DocumentPathExpression pathToSourceProperty) {
            this.pathToSourceProperty = pathToSourceProperty;
            return self();
        }

        /**
         * @return {@code this}.
         */
        public B lookupFailBehaviour(final MappingErrorBehaviour lookupFailBehaviour) {
            this.lookupFailBehaviour = lookupFailBehaviour;
            return self();
        }

        @Override
        public String toString() {
            return "AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder(super=" + super.toString()
                    + ", pathToSourceProperty=" + this.pathToSourceProperty + ", lookupFailBehaviour="
                    + this.lookupFailBehaviour + ")";
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected AbstractPropertyToColumnMapping(final AbstractPropertyToColumnMapping.Builder<?, ?> builder) {
        super(builder);
        this.pathToSourceProperty = builder.pathToSourceProperty;
        this.lookupFailBehaviour = builder.lookupFailBehaviour;
    }

    @Override
    public String toString() {
        return "AbstractPropertyToColumnMapping(super=" + super.toString() + ", pathToSourceProperty="
                + this.getPathToSourceProperty() + ", lookupFailBehaviour=" + this.getLookupFailBehaviour() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(pathToSourceProperty, lookupFailBehaviour);
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
        final AbstractPropertyToColumnMapping other = (AbstractPropertyToColumnMapping) obj;
        return Objects.equals(pathToSourceProperty, other.pathToSourceProperty)
                && lookupFailBehaviour == other.lookupFailBehaviour;
    }
}
