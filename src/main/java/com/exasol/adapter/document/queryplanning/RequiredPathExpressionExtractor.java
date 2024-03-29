package com.exasol.adapter.document.queryplanning;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.mapping.*;

/**
 * This class extracts the required {@link DocumentPathExpression}s that must be fetched from the remote database for
 * solving the given query.
 */
public class RequiredPathExpressionExtractor {

    /**
     * Get a set of properties that must be fetched from the remote database.
     * 
     * @param requiredColumns stream of required columns
     * @return set of required properties
     */
    public Set<DocumentPathExpression> getRequiredProperties(final Stream<? extends ColumnMapping> requiredColumns) {
        return requiredColumns.map(this::getRequiredProperty).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private DocumentPathExpression getRequiredProperty(final ColumnMapping columnMapping) {
        final Visitor visitor = new Visitor();
        columnMapping.accept(visitor);
        return visitor.requiredPathExpression;
    }

    private static class Visitor implements ColumnMappingVisitor {
        private DocumentPathExpression requiredPathExpression;

        @Override
        public void visit(final PropertyToColumnMapping propertyToColumnMapping) {
            this.requiredPathExpression = propertyToColumnMapping.getPathToSourceProperty();
        }

        @Override
        public void visit(final IterationIndexColumnMapping iterationIndexColumnDefinition) {
            this.requiredPathExpression = iterationIndexColumnDefinition.getTablesPath();
        }

        @Override
        public void visit(final SourceReferenceColumnMapping sourceReferenceColumnMapping) {
            this.requiredPathExpression = null;
        }
    }
}
