package com.exasol.adapter.document.mapping;

import java.util.Objects;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.metadata.DataType;

/**
 * This class defines a column that maps the array index of a nested list. Such columns are useful for nested tables
 * that do not have an key.
 */

public final class IterationIndexColumnMapping extends AbstractColumnMapping {
    private static final long serialVersionUID = -5720702055496015560L;
    /** @serial */
    private final DocumentPathExpression tablesPath;

    /**
     * Create an instance of {@link IterationIndexColumnMapping}.
     *
     * @param exasolColumnName name of the Exasol column
     * @param tablesPath       the path to the array that's row index is modeled using this column
     */
    public IterationIndexColumnMapping(final String exasolColumnName, final DocumentPathExpression tablesPath) {
        this(builder().exasolColumnName(exasolColumnName).tablesPath(tablesPath));
    }

    /**
     * Get the path to the array that's row index is modeled using this column.
     * 
     * @return path to the array
     */
    public DocumentPathExpression getTablesPath() {
        return tablesPath;
    }

    @Override
    public DataType getExasolDataType() {
        return DataType.createDecimal(9, 0);
    }

    @Override
    public boolean isExasolColumnNullable() {
        return false;
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    @Override
    public void accept(final ColumnMappingVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Builder for {@link IterationIndexColumnMapping}.
     */
    public abstract static class Builder<C extends IterationIndexColumnMapping, B extends IterationIndexColumnMapping.Builder<C, B>>
            extends AbstractColumnMapping.Builder<C, B> {
        private DocumentPathExpression tablesPath;

        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            IterationIndexColumnMapping.Builder.fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }

        private static void fillValuesFromInstanceIntoBuilder(final IterationIndexColumnMapping instance,
                final IterationIndexColumnMapping.Builder<?, ?> builder) {
            builder.tablesPath(instance.tablesPath);
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        /**
         * @param tablesPath table path
         * @return {@code this}.
         */
        public B tablesPath(final DocumentPathExpression tablesPath) {
            this.tablesPath = tablesPath;
            return self();
        }

        @Override
        public String toString() {
            return "IterationIndexColumnMapping.IterationIndexColumnMappingBuilder(super=" + super.toString()
                    + ", tablesPath=" + this.tablesPath + ")";
        }
    }

    private static final class BuilderImpl extends
            IterationIndexColumnMapping.Builder<IterationIndexColumnMapping, IterationIndexColumnMapping.BuilderImpl> {

        private BuilderImpl() {
        }

        @Override
        protected IterationIndexColumnMapping.BuilderImpl self() {
            return this;
        }

        /**
         * Build a new instance.
         * 
         * @return new instance
         */
        @Override
        public IterationIndexColumnMapping build() {
            return new IterationIndexColumnMapping(this);
        }
    }

    /**
     * Creates a new instance from a builder.
     * 
     * @param builder builder
     */
    protected IterationIndexColumnMapping(final IterationIndexColumnMapping.Builder<?, ?> builder) {
        super(builder);
        this.tablesPath = builder.tablesPath;
    }

    /**
     * Create a new builder for {@link IterationIndexColumnMapping}.
     * 
     * @return a new builder
     */
    @SuppressWarnings("java:S1452") // Generic wildcard type is ok here
    public static IterationIndexColumnMapping.Builder<?, ?> builder() {
        return new IterationIndexColumnMapping.BuilderImpl();
    }

    private IterationIndexColumnMapping.Builder<?, ?> toBuilder() {
        return new IterationIndexColumnMapping.BuilderImpl().fillValuesFrom(this);
    }

    @Override
    public String toString() {
        return "IterationIndexColumnMapping(super=" + super.toString() + ", tablesPath=" + this.getTablesPath() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(tablesPath);
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
        final IterationIndexColumnMapping other = (IterationIndexColumnMapping) obj;
        return Objects.equals(tablesPath, other.tablesPath);
    }
}
