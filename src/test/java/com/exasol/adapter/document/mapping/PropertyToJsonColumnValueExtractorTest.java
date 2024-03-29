package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.MappingErrorBehaviour.ABORT;
import static com.exasol.adapter.document.edml.MappingErrorBehaviour.NULL;
import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;

class PropertyToJsonColumnValueExtractorTest {

    private static PropertyToJsonColumnMapping.Builder<?, ?> getDefaultMappingBuilder() {
        return configureExampleMapping(PropertyToJsonColumnMapping.builder())//
                .varcharColumnSize(254)//
                .overflowBehaviour(ABORT);
    }

    static Stream<Arguments> conversionTestCases() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("test"), "\"test\""), //
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("2")), "2"), //
                Arguments.of(new BooleanHolderNode(true), "true"), //
                Arguments.of(new BooleanHolderNode(false), "false"), //
                Arguments.of(new BinaryHolderNode("abc".getBytes()), "\"YWJj\""), //
                Arguments.of(new DoubleHolderNode(1.2), "1.2"), //
                Arguments.of(new ArrayHolderNode(List.of(new StringHolderNode("test"))), "[\"test\"]"), //
                Arguments.of(new ArrayHolderNode(List.of(new NullHolderNode())), "[null]"), //
                Arguments.of(new ObjectHolderNode(Map.of("test", new BooleanHolderNode(false))), "{\"test\":false}"), //
                Arguments.of(new ObjectHolderNode(Map.of("test", new NullHolderNode())), "{\"test\":null}"), //
                Arguments.of(new DateHolderNode(new Date(1632212318000L)), "\"2021-09-21\""), //
                Arguments.of(new TimestampHolderNode(new Timestamp(1632212318000L)), "\"2021-09-21T08:18:38Z\"") //
        );
    }

    @ParameterizedTest
    @MethodSource("conversionTestCases")
    void testConversion(final DocumentNode input, final String expectedOutput) {
        final Object result = new PropertyToJsonColumnValueExtractor(getDefaultMappingBuilder().build())
                .mapValue(input);
        assertThat(result, equalTo(expectedOutput));
    }

    @Test
    void testConvertNull() {
        final Object result = new PropertyToJsonColumnValueExtractor(getDefaultMappingBuilder().build())
                .mapValue(new NullHolderNode());
        assertThat(result, is(nullValue()));
    }

    @Test
    void testOverflowAbort() {
        final PropertyToJsonColumnMapping column = getDefaultMappingBuilder().varcharColumnSize(2).build();
        final PropertyToJsonColumnValueExtractor valueExtractor = new PropertyToJsonColumnValueExtractor(column);
        final StringHolderNode testValue = new StringHolderNode("test");
        final OverflowException exception = assertThrows(OverflowException.class,
                () -> valueExtractor.mapValue(testValue));
        assertThat(exception.getMessage(), startsWith("E-VSD-35"));
    }

    @Test
    void testOverflowNull() {
        final PropertyToJsonColumnMapping column = getDefaultMappingBuilder().varcharColumnSize(2)
                .overflowBehaviour(NULL).build();
        final PropertyToJsonColumnValueExtractor valueExtractor = new PropertyToJsonColumnValueExtractor(column);
        final StringHolderNode testValue = new StringHolderNode("test");
        assertThat(valueExtractor.mapValue(testValue), is(nullValue()));
    }
}
