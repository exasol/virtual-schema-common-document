package com.exasol.adapter.document.mapping;

/**
 * Factory for {@link ColumnValueExtractor}s. In contrast to {@link ColumnValueExtractorFactory} this interface is for
 * the subset of {@link PropertyToColumnMapping} columns.
 */
@java.lang.SuppressWarnings("squid:S119") // DocumentVisitorType does not fit naming conventions.
public class PropertyToColumnValueExtractorFactory {
    private PropertyToColumnValueExtractorFactory() {
        // empty on purpose
    }

    /**
     * Builds a ValueMapper fitting into a ColumnMappingDefinition.
     *
     * @param column ColumnMappingDefinition for which to build the ValueMapper
     * @return built ValueMapper
     */
    static ColumnValueExtractor getValueExtractorForColumn(final PropertyToColumnMapping column) {
        final DispatchVisitor dispatchVisitor = new DispatchVisitor();
        column.accept(dispatchVisitor);
        return dispatchVisitor.getResult();
    }

    private static class DispatchVisitor implements PropertyToColumnMappingVisitor {
        private ColumnValueExtractor result;

        @Override
        public void visit(final PropertyToVarcharColumnMapping columnDefinition) {
            this.result = new PropertyToVarcharColumnValueExtractor(columnDefinition);
        }

        @Override
        public void visit(final PropertyToJsonColumnMapping columnDefinition) {
            this.result = new PropertyToJsonColumnValueExtractor(columnDefinition);
        }

        @Override
        public void visit(final PropertyToDecimalColumnMapping columnDefinition) {
            this.result = new PropertyToDecimalColumnValueExtractor(columnDefinition);
        }

        @Override
        public void visit(final PropertyToDoubleColumnMapping columnDefinition) {
            this.result = new PropertyToDoubleColumnValueExtractor(columnDefinition);
        }

        @Override
        public void visit(final PropertyToBoolColumnMapping columnDefinition) {
            this.result = new PropertyToBoolColumnValueExtractor(columnDefinition);
        }

        /**
         * Get the result.
         * 
         * @return result
         */
        public ColumnValueExtractor getResult() {
            return this.result;
        }
    }
}
