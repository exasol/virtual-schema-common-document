package com.exasol.adapter.document.mapping.converter;

import java.util.*;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.mapping.*;

import lombok.Getter;

/**
 * This class converts an {@link EdmlDefinition} into a {@link StagingTableMapping}.
 */
class EdmlToStagingTableMappingConverter {

    /**
     * Convert an {@link EdmlDefinition} into a {@link StagingTableMapping}.
     * 
     * @param edmlDefinition {@link EdmlDefinition} to convert
     * @return converted
     */
    StagingTableMapping convert(final EdmlDefinition edmlDefinition) {
        final MappingDefinitionConvertVisitor visitor = new MappingDefinitionConvertVisitor(edmlDefinition.getSource(),
                edmlDefinition.getDestinationTable(), DocumentPathExpression.builder());
        edmlDefinition.getMapping().accept(visitor);
        return new StagingTableMapping(edmlDefinition.getDestinationTable(), edmlDefinition.getSource(),
                visitor.getColumns(), DocumentPathExpression.empty(), visitor.getNestedTables());
    }

    private static class MappingDefinitionConvertVisitor implements MappingDefinitionVisitor {
        private final String source;
        private final String destinationTableName;
        private final DocumentPathExpression.Builder path;
        @Getter
        private final List<ColumnWithKeyInfo> columns;
        @Getter
        private final List<StagingTableMapping> nestedTables;

        private MappingDefinitionConvertVisitor(final String source, final String destinationTableName,
                final DocumentPathExpression.Builder path) {
            this.source = source;
            this.destinationTableName = destinationTableName;
            this.path = path;
            this.columns = new ArrayList<>();
            this.nestedTables = new ArrayList<>();
        }

        @Override
        public void visit(final Fields fields) {
            for (final Map.Entry<String, MappingDefinition> field : fields.getFields().entrySet()) {
                final DocumentPathExpression.Builder childPath = new DocumentPathExpression.Builder(this.path)
                        .addObjectLookup(field.getKey());
                final MappingDefinitionConvertVisitor childVisitor = new MappingDefinitionConvertVisitor(this.source,
                        this.destinationTableName, childPath);
                field.getValue().accept(childVisitor);
                this.columns.addAll(childVisitor.getColumns());
                this.nestedTables.addAll(childVisitor.getNestedTables());
            }
        }

        @Override
        public void visit(final ToDecimalMapping source) {
            final PropertyToDecimalColumnMapping column = PropertyToDecimalColumnMapping.builder()//
                    .pathToSourceProperty(this.path.build())//
                    .decimalPrecision(source.getDecimalPrecision())//
                    .decimalScale(source.getDecimalScale())//
                    .exasolColumnName(source.getDestinationName())//
                    .notNumericBehaviour(source.getNotNumericBehaviour())//
                    .overflowBehaviour(source.getOverflowBehaviour())//
                    .lookupFailBehaviour(convertRequired(source.isRequired()))//
                    .build();
            this.columns.add(new ColumnWithKeyInfo(column, source.getKey()));
        }

        private MappingErrorBehaviour convertRequired(final boolean isRequired) {
            if (isRequired) {
                return MappingErrorBehaviour.ABORT;
            } else {
                return MappingErrorBehaviour.NULL;
            }
        }

        @Override
        public void visit(final ToJsonMapping source) {
            final PropertyToJsonColumnMapping column = PropertyToJsonColumnMapping.builder()//
                    .pathToSourceProperty(this.path.build())//
                    .varcharColumnSize(source.getVarcharColumnSize())//
                    .exasolColumnName(source.getDestinationName())//
                    .overflowBehaviour(source.getOverflowBehaviour())//
                    .lookupFailBehaviour(convertRequired(source.isRequired()))//
                    .build();
            this.columns.add(new ColumnWithKeyInfo(column, source.getKey()));
        }

        @Override
        public void visit(final ToVarcharMapping source) {
            final PropertyToVarcharColumnMapping column = PropertyToVarcharColumnMapping.builder()//
                    .pathToSourceProperty(this.path.build())//
                    .varcharColumnSize(source.getVarcharColumnSize())//
                    .exasolColumnName(source.getDestinationName())//
                    .lookupFailBehaviour(convertRequired(source.isRequired()))//
                    .nonStringBehaviour(source.getNonStringBehaviour())//
                    .overflowBehaviour(source.getOverflowBehaviour())//
                    .build();
            this.columns.add(new ColumnWithKeyInfo(column, source.getKey()));
        }

        @Override
        public void visit(final ToTableMapping toTableMapping) {
            final DocumentPathExpression.Builder childPath = new DocumentPathExpression.Builder(this.path)
                    .addArrayAll();
            final MappingDefinitionConvertVisitor visitor = new MappingDefinitionConvertVisitor(this.source,
                    this.destinationTableName, childPath);
            toTableMapping.getMapping().accept(visitor);
            final String nestedDestinationName = toTableMapping.getDestinationTable();
            this.nestedTables.add(new StagingTableMapping(nestedDestinationName, this.source, visitor.getColumns(),
                    childPath.build(), visitor.getNestedTables()));
        }
    }
}
