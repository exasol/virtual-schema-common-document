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

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class AbstractPropertyToColumnMappingBuilder<C extends AbstractPropertyToColumnMapping, B extends AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<C, B>>
            extends AbstractColumnMapping.AbstractColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private DocumentPathExpression pathToSourceProperty;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private MappingErrorBehaviour lookupFailBehaviour;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder
                    .$fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final AbstractPropertyToColumnMapping instance,
                final AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<?, ?> b) {
            b.pathToSourceProperty(instance.pathToSourceProperty);
            b.lookupFailBehaviour(instance.lookupFailBehaviour);
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
        public B pathToSourceProperty(final DocumentPathExpression pathToSourceProperty) {
            this.pathToSourceProperty = pathToSourceProperty;
            return self();
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public B lookupFailBehaviour(final MappingErrorBehaviour lookupFailBehaviour) {
            this.lookupFailBehaviour = lookupFailBehaviour;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder(super=" + super.toString()
                    + ", pathToSourceProperty=" + this.pathToSourceProperty + ", lookupFailBehaviour="
                    + this.lookupFailBehaviour + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected AbstractPropertyToColumnMapping(
            final AbstractPropertyToColumnMapping.AbstractPropertyToColumnMappingBuilder<?, ?> b) {
        super(b);
        this.pathToSourceProperty = b.pathToSourceProperty;
        this.lookupFailBehaviour = b.lookupFailBehaviour;
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
