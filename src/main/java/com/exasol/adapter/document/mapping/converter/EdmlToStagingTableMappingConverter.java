package com.exasol.adapter.document.mapping.converter;

import java.util.*;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.mapping.*;
import com.exasol.errorreporting.ExaError;

/**
 * This class converts an {@link EdmlDefinition} into a {@link StagingTableMapping}.
 */
class EdmlToStagingTableMappingConverter {

    /**
     * Convert an {@link EdmlDefinition} into a {@link StagingTableMapping}.
     *
     * @param edmlDefinition {@link EdmlDefinition} to convert
     * @return converted definition
     */
    StagingTableMapping convert(final EdmlDefinition edmlDefinition) {
        if (edmlDefinition.getMapping() == null) {
            throw new IllegalStateException(ExaError.messageBuilder("E-VSD-103")
                    .message("EDML definition does not contain a mapping").ticketMitigation().toString());
        }
        final MappingDefinitionConverterVisitor visitor = new MappingDefinitionConverterVisitor(
                edmlDefinition.getSource(), edmlDefinition.getDestinationTable(), DocumentPathExpression.builder(),
                edmlDefinition.getAdditionalConfiguration());
        edmlDefinition.getMapping().accept(visitor);
        return new StagingTableMapping(edmlDefinition.getDestinationTable(), edmlDefinition.getSource(),
                edmlDefinition.getAdditionalConfiguration(), visitor.getColumns(), DocumentPathExpression.empty(),
                visitor.getNestedTables());
    }

    private static class MappingDefinitionConverterVisitor implements MappingDefinitionVisitor {
        private final String source;
        private final String destinationTableName;
        private final String additionalConfiguration;
        private final DocumentPathExpression.Builder path;
        private final List<ColumnWithKeyInfo> columns;
        private final List<StagingTableMapping> nestedTables;

        private MappingDefinitionConverterVisitor(final String source, final String destinationTableName,
                final DocumentPathExpression.Builder path, final String additionalConfiguration) {
            this.source = source;
            this.destinationTableName = destinationTableName;
            this.additionalConfiguration = additionalConfiguration;
            this.path = path;
            this.columns = new ArrayList<>();
            this.nestedTables = new ArrayList<>();
        }

        private List<ColumnWithKeyInfo> getColumns() {
            return columns;
        }

        private List<StagingTableMapping> getNestedTables() {
            return nestedTables;
        }

        @Override
        public void visit(final Fields fields) {
            for (final Map.Entry<String, MappingDefinition> field : fields.getFieldsMap().entrySet()) {
                final DocumentPathExpression.Builder childPath = new DocumentPathExpression.Builder(this.path)
                        .addObjectLookup(field.getKey());
                final MappingDefinitionConverterVisitor childVisitor = new MappingDefinitionConverterVisitor(
                        this.source, this.destinationTableName, childPath, this.additionalConfiguration);
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
        public void visit(final ToDoubleMapping source) {
            final PropertyToDoubleColumnMapping column = PropertyToDoubleColumnMapping.builder()//
                    .pathToSourceProperty(this.path.build())//
                    .exasolColumnName(source.getDestinationName())//
                    .notNumericBehaviour(source.getNotNumericBehaviour())//
                    .overflowBehaviour(source.getOverflowBehaviour())//
                    .lookupFailBehaviour(convertRequired(source.isRequired()))//
                    .build();
            this.columns.add(new ColumnWithKeyInfo(column, source.getKey()));
        }

        @Override
        public void visit(final ToBoolMapping source) {
            final PropertyToBoolColumnMapping column = PropertyToBoolColumnMapping.builder()//
                    .pathToSourceProperty(this.path.build())//
                    .exasolColumnName(source.getDestinationName())//
                    .notBooleanBehavior(source.getNotBooleanBehavior())//
                    .lookupFailBehaviour(convertRequired(source.isRequired()))//
                    .build();
            this.columns.add(new ColumnWithKeyInfo(column, source.getKey()));
        }

        @Override
        public void visit(final ToDateMapping source) {
            final PropertyToDateColumnMapping column = PropertyToDateColumnMapping.builder()//
                    .pathToSourceProperty(this.path.build())//
                    .exasolColumnName(source.getDestinationName())//
                    .notDateBehaviour(source.getNotDateBehavior())//
                    .lookupFailBehaviour(convertRequired(source.isRequired()))//
                    .build();
            this.columns.add(new ColumnWithKeyInfo(column, source.getKey()));
        }

        @Override
        public void visit(final ToTimestampMapping source) {
            final PropertyToTimestampColumnMapping column = PropertyToTimestampColumnMapping.builder()//
                    .pathToSourceProperty(this.path.build())//
                    .exasolColumnName(source.getDestinationName())//
                    .notTimestampBehaviour(source.getNotTimestampBehavior())//
                    .useTimestampWithLocalTimezoneType(source.isUseTimestampWithLocalTimezoneType())
                    .lookupFailBehaviour(convertRequired(source.isRequired()))//
                    .build();
            this.columns.add(new ColumnWithKeyInfo(column, source.getKey()));
        }

        @Override
        public void visit(final ToTableMapping toTableMapping) {
            final DocumentPathExpression.Builder childPath = new DocumentPathExpression.Builder(this.path)
                    .addArrayAll();
            final MappingDefinitionConverterVisitor visitor = new MappingDefinitionConverterVisitor(this.source,
                    this.destinationTableName, childPath, this.additionalConfiguration);
            toTableMapping.getMapping().accept(visitor);
            final String nestedDestinationName = toTableMapping.getDestinationTable();
            this.nestedTables.add(new StagingTableMapping(nestedDestinationName, this.source,
                    this.additionalConfiguration, visitor.getColumns(), childPath.build(), visitor.getNestedTables()));

        }
    }
}
