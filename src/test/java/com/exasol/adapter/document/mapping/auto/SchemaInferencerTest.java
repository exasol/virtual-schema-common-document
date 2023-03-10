package com.exasol.adapter.document.mapping.auto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.adapter.document.edml.*;
import com.exasol.adapter.document.edml.EdmlDefinition.EdmlDefinitionBuilder;

@ExtendWith(MockitoExtension.class)
class SchemaInferencerTest {

    private static final String ADDITIONAL_CONFIG = "additionalConfig";
    private static final String DESCRIPTION = "descr";
    private static final String DESTINATION = "dest";
    private static final String SOURCE = "mySource";
    @Mock
    SchemaFetcher schemaFetcherMock;
    private SchemaInferencer inferencer;

    @BeforeEach
    void setUp() {
        inferencer = new SchemaInferencer(schemaFetcherMock);
    }

    @Test
    void mappingPresent() {
        final EdmlDefinition definition = createDefinition(createMapping());
        final EdmlDefinition result = inferencer.inferSchema(definition);
        assertThat(result, sameInstance(definition));
    }

    @Test
    void mappingNotPresent() {
        final EdmlDefinition definition = createDefinition(null);
        final MappingDefinition mapping = createMapping();
        when(schemaFetcherMock.fetchSchema(SOURCE)).thenReturn(Optional.of(mapping));
        final EdmlDefinition result = inferencer.inferSchema(definition);
        assertAll(() -> assertThat(result, not(sameInstance(definition))),
                () -> assertThat(result.getMapping(), sameInstance(mapping)),
                () -> assertThat(result.getAdditionalConfiguration(), equalTo(ADDITIONAL_CONFIG)),
                () -> assertThat(result.getSource(), equalTo(SOURCE)),
                () -> assertThat(result.getDestinationTable(), equalTo(DESTINATION)),
                () -> assertThat(result.getDescription(), equalTo(DESCRIPTION)),
                () -> assertThat(result.isAddSourceReferenceColumn(), is(true)));
    }

    @Test
    void mappingNotPresentSourceNotSupported() {
        final EdmlDefinition definition = createDefinition(null);
        when(schemaFetcherMock.fetchSchema(SOURCE)).thenReturn(Optional.empty());
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> inferencer.inferSchema(definition));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-101: This virtual schema does not support auto inference for source 'mySource'. Please specify the 'mapping' element in the JSON EDML definition."));
    }

    @Test
    void mappingNotPresentFetchingFails() {
        final EdmlDefinition definition = createDefinition(null);
        when(schemaFetcherMock.fetchSchema(SOURCE)).thenThrow(new RuntimeException("expected"));
        final Exception exception = assertThrows(IllegalStateException.class, () -> inferencer.inferSchema(definition));
        assertThat(exception.getMessage(),
                startsWith("E-VSD-102: Schema auto inference for source 'mySource' failed. Known"));
    }

    private MappingDefinition createMapping() {
        return Fields.builder().mapField("field", ToVarcharMapping.builder().build()).build();
    }

    private EdmlDefinition createDefinition(final MappingDefinition mapping) {
        final EdmlDefinitionBuilder builder = EdmlDefinition.builder().source(SOURCE).destinationTable(DESTINATION)
                .addSourceReferenceColumn(true).additionalConfiguration(ADDITIONAL_CONFIG).description(DESCRIPTION);
        if (mapping != null) {
            builder.mapping(mapping);
        }
        return builder.build();
    }
}
