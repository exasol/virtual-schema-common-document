package com.exasol.adapter.document.mapping.auto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
        this.inferencer = new SchemaInferencer(this.schemaFetcherMock);
    }

    @Test
    void mappingPresent() {
        final EdmlDefinition definition = createDefinition(createMapping());
        final EdmlDefinition result = this.inferencer.inferSchema(definition);
        assertThat(result, sameInstance(definition));
    }

    @Test
    void mappingNotPresent() {
        final EdmlDefinition definition = createDefinition(null);
        final MappingDefinition mapping = createMapping();
        simulatedDetectedSchema(InferredMappingDefinition.builder(mapping)
                .additionalConfiguration("ignored additional config").description("ignored description"));
        final EdmlDefinition result = this.inferencer.inferSchema(definition);
        assertAll(() -> assertThat(result, not(sameInstance(definition))),
                () -> assertThat(result.getMapping(), sameInstance(mapping)),
                () -> assertThat(result.getAdditionalConfiguration(), equalTo(ADDITIONAL_CONFIG)),
                () -> assertThat(result.getSource(), equalTo(SOURCE)),
                () -> assertThat(result.getDestinationTable(), equalTo(DESTINATION)),
                () -> assertThat(result.getDescription(), equalTo(DESCRIPTION)),
                () -> assertThat(result.isAddSourceReferenceColumn(), is(true)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void additionalConfigurationDetected(final String existingConfiguration) {
        final EdmlDefinition definition = emptyDefinition().additionalConfiguration(existingConfiguration).build();
        simulatedDetectedSchema(
                InferredMappingDefinition.builder(createMapping()).additionalConfiguration("detected config"));
        final EdmlDefinition result = this.inferencer.inferSchema(definition);
        assertThat(result.getAdditionalConfiguration(), equalTo("detected config"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void descriptionDetected(final String existingDescription) {
        final EdmlDefinition definition = emptyDefinition().description(existingDescription).build();
        simulatedDetectedSchema(InferredMappingDefinition.builder(createMapping()).description("detected description"));
        final EdmlDefinition result = this.inferencer.inferSchema(definition);
        assertThat(result.getDescription(), equalTo("detected description"));
    }

    private void simulatedDetectedSchema(final InferredMappingDefinition.Builder builder) {
        when(this.schemaFetcherMock.fetchSchema(eq(SOURCE), any())).thenReturn(Optional.of(builder.build()));
    }

    @Test
    void mappingNotPresentSourceNotSupported() {
        final EdmlDefinition definition = createDefinition(null);
        when(this.schemaFetcherMock.fetchSchema(eq(SOURCE), any())).thenReturn(Optional.empty());
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> this.inferencer.inferSchema(definition));
        assertThat(exception.getMessage(), equalTo(
                "E-VSD-101: This virtual schema does not support auto inference for source 'mySource'. Please specify the 'mapping' element in the JSON EDML definition."));
    }

    @Test
    void mappingNotPresentFetchingFails() {
        final EdmlDefinition definition = createDefinition(null);
        when(this.schemaFetcherMock.fetchSchema(eq(SOURCE), any())).thenThrow(new RuntimeException("expected"));
        final Exception exception = assertThrows(IllegalStateException.class,
                () -> this.inferencer.inferSchema(definition));
        assertThat(exception.getMessage(),
                startsWith("E-VSD-102: Schema auto inference for source 'mySource' failed. Known"));
    }

    private MappingDefinition createMapping() {
        return Fields.builder().mapField("field", ToVarcharMapping.builder().build()).build();
    }

    private EdmlDefinitionBuilder emptyDefinition() {
        return EdmlDefinition.builder().source(SOURCE).destinationTable(DESTINATION).addSourceReferenceColumn(true)
                .additionalConfiguration(null).description(null);
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
