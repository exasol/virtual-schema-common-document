package com.exasol.adapter.document.mapping.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.document.documentpath.DocumentPathExpression;
import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.edml.EdmlDefinition.EdmlDefinitionBuilder;
import com.exasol.adapter.document.mapping.ColumnMapping;
import com.exasol.adapter.document.mapping.PropertyToTimestampColumnMapping;

class EdmlToStagingTableMappingConverterTest {

    private static final String FIELD_NAME = "field";
    private static final DocumentPathExpression pathToSourceProperty = DocumentPathExpression.builder()
            .addObjectLookup(FIELD_NAME).build();

    static Stream<Arguments> mappings() {
        return Stream.of(mappingTest("timestamp default values", ToTimestampMapping.builder().build(),
                PropertyToTimestampColumnMapping.builder().pathToSourceProperty(pathToSourceProperty)
                        .lookupFailBehaviour(MappingErrorBehaviour.NULL)
                        .notTimestampBehaviour(ConvertableMappingErrorBehaviour.ABORT).secondsPrecision(6).build()),
                mappingTest("timestamp custom values",
                        ToTimestampMapping.builder()
                                .notTimestampBehavior(ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT)
                                .secondsPrecision(7).build(),
                        PropertyToTimestampColumnMapping.builder().pathToSourceProperty(pathToSourceProperty)
                                .lookupFailBehaviour(MappingErrorBehaviour.NULL)
                                .notTimestampBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_ABORT)
                                .secondsPrecision(7).build()));
    }

    private static Arguments mappingTest(final String testName, final MappingDefinition field,
            final ColumnMapping expectedMapping) {
        return Arguments.of(testName, field, expectedMapping);
    }

    @ParameterizedTest(name = "Convert mapping: {0}")
    @MethodSource("mappings")
    void visitTimestamp(final String testName, final MappingDefinition field, final ColumnMapping expectedMapping) {
        final List<ColumnWithKeyInfo> columns = convert(defaultEdml(field)).getColumns();
        assertThat(columns, hasSize(1));
        final ColumnWithKeyInfo column = columns.get(0);
        assertThat(column.getKey(), equalTo(KeyType.NONE));
        assertThat(column.getColumn(), equalTo(expectedMapping));
    }

    private EdmlDefinitionBuilder defaultEdml(final MappingDefinition field) {
        return EdmlDefinition.builder().source("source").destinationTable("destination")
                .additionalConfiguration("additional").mapping(Fields.builder().mapField(FIELD_NAME, field).build());
    }

    private StagingTableMapping convert(final EdmlDefinitionBuilder edml) {
        return new EdmlToStagingTableMappingConverter().convert(edml.build());
    }
}
