package com.exasol.adapter.document.mapping;

/**
 * This class is a factory for {@link ColumnValueExtractor}s.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class ColumnValueExtractorFactory {
    /**
     * Builds a {@link ColumnValueExtractor} for a given {@link ColumnMapping}.
     * 
     * @param columnMapping {@link ColumnMapping} that builds the {@link ColumnValueExtractor}.
     * @return built {@link ColumnValueExtractor}
     */
    public ColumnValueExtractor getValueExtractorForColumn(final ColumnMapping columnMapping) {
        final Visitor visitor = new Visitor();
        columnMapping.accept(visitor);
        return visitor.getExtractor();
    }

    private static class Visitor implements ColumnMappingVisitor {
        private ColumnValueExtractor extractor;

        private Visitor() {
        }

        @Override
        public void visit(final PropertyToColumnMapping propertyToColumnMapping) {
            this.extractor = PropertyToColumnValueExtractorFactory.getValueExtractorForColumn(propertyToColumnMapping);
        }

        @Override
        public void visit(final IterationIndexColumnMapping iterationIndexColumnDefinition) {
            this.extractor = new IterationIndexColumnValueExtractor(iterationIndexColumnDefinition);
        }

        @Override
        public void visit(final SourceReferenceColumnMapping sourceReferenceColumnMapping) {
            this.extractor = new SourceReferenceColumnValueExtractor();
        }

        public ColumnValueExtractor getExtractor() {
            return this.extractor;
        }
    }
}
