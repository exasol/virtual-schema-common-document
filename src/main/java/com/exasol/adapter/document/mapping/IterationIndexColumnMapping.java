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

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static abstract class IterationIndexColumnMappingBuilder<C extends IterationIndexColumnMapping, B extends IterationIndexColumnMapping.IterationIndexColumnMappingBuilder<C, B>>
            extends AbstractColumnMapping.AbstractColumnMappingBuilder<C, B> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private DocumentPathExpression tablesPath;

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected B $fillValuesFrom(final C instance) {
            super.$fillValuesFrom(instance);
            IterationIndexColumnMapping.IterationIndexColumnMappingBuilder.$fillValuesFromInstanceIntoBuilder(instance,
                    this);
            return self();
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private static void $fillValuesFromInstanceIntoBuilder(final IterationIndexColumnMapping instance,
                final IterationIndexColumnMapping.IterationIndexColumnMappingBuilder<?, ?> b) {
            b.tablesPath(instance.tablesPath);
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
        public B tablesPath(final DocumentPathExpression tablesPath) {
            this.tablesPath = tablesPath;
            return self();
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "IterationIndexColumnMapping.IterationIndexColumnMappingBuilder(super=" + super.toString()
                    + ", tablesPath=" + this.tablesPath + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    private static final class IterationIndexColumnMappingBuilderImpl extends
            IterationIndexColumnMapping.IterationIndexColumnMappingBuilder<IterationIndexColumnMapping, IterationIndexColumnMapping.IterationIndexColumnMappingBuilderImpl> {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private IterationIndexColumnMappingBuilderImpl() {
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        protected IterationIndexColumnMapping.IterationIndexColumnMappingBuilderImpl self() {
            return this;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public IterationIndexColumnMapping build() {
            return new IterationIndexColumnMapping(this);
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected IterationIndexColumnMapping(
            final IterationIndexColumnMapping.IterationIndexColumnMappingBuilder<?, ?> b) {
        super(b);
        this.tablesPath = b.tablesPath;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static IterationIndexColumnMapping.IterationIndexColumnMappingBuilder<?, ?> builder() {
        return new IterationIndexColumnMapping.IterationIndexColumnMappingBuilderImpl();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public IterationIndexColumnMapping.IterationIndexColumnMappingBuilder<?, ?> toBuilder() {
        return new IterationIndexColumnMapping.IterationIndexColumnMappingBuilderImpl().$fillValuesFrom(this);
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
