package com.exasol.adapter.document.mapping;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.MappingErrorBehaviour;
import com.exasol.adapter.metadata.DataType;

public class MockPropertyToColumnMapping extends AbstractPropertyToColumnMapping {
    private static final long serialVersionUID = 5927405036134725056L;

    public MockPropertyToColumnMapping(final String destinationName, final DocumentPathExpression sourcePath,
            final MappingErrorBehaviour lookupFailBehaviour) {
        this(builder().exasolColumnName(destinationName).pathToSourceProperty(sourcePath)
                .lookupFailBehaviour(lookupFailBehaviour));
    }

    @Override
    public DataType getExasolDataType() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isExasolColumnNullable() {
        return false;
    }

    @Override
    public void accept(final PropertyToColumnMappingVisitor visitor) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ColumnMapping withNewExasolName(final String newExasolName) {
        return this.toBuilder().exasolColumnName(newExasolName).build();
    }

    public abstract static class MockPropertyToColumnMappingBuilder<C extends MockPropertyToColumnMapping, B extends MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilder<C, B>>
            extends AbstractPropertyToColumnMapping.Builder<C, B> {
        @Override
        protected B fillValuesFrom(final C instance) {
            super.fillValuesFrom(instance);
            MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilder.$fillValuesFromInstanceIntoBuilder(instance,
                    this);
            return self();
        }

        private static void $fillValuesFromInstanceIntoBuilder(final MockPropertyToColumnMapping instance,
                final MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilder<?, ?> b) {
        }

        @Override
        protected abstract B self();

        @Override
        public abstract C build();

        @Override
        public String toString() {
            return "MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilder(super=" + super.toString() + ")";
        }
    }

    private static final class MockPropertyToColumnMappingBuilderImpl extends
            MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilder<MockPropertyToColumnMapping, MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilderImpl> {

        private MockPropertyToColumnMappingBuilderImpl() {
        }

        @Override
        protected MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilderImpl self() {
            return this;
        }

        @Override
        public MockPropertyToColumnMapping build() {
            return new MockPropertyToColumnMapping(this);
        }
    }

    protected MockPropertyToColumnMapping(
            final MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilder<?, ?> builder) {
        super(builder);
    }

    public static MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilder<?, ?> builder() {
        return new MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilderImpl();
    }

    private MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilder<?, ?> toBuilder() {
        return new MockPropertyToColumnMapping.MockPropertyToColumnMappingBuilderImpl().fillValuesFrom(this);
    }

    @Override
    public String toString() {
        return "MockPropertyToColumnMapping(super=" + super.toString() + ")";
    }

    @Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MockPropertyToColumnMapping)) {
            return false;
        }
        final MockPropertyToColumnMapping other = (MockPropertyToColumnMapping) o;
        if (!other.canEqual((java.lang.Object) this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof MockPropertyToColumnMapping;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
