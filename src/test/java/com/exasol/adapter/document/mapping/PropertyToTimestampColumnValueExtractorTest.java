package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;

class PropertyToTimestampColumnValueExtractorTest {
    private static PropertyToTimestampColumnMapping.Builder<?, ?> commonMappingBuilder() {
        return configureExampleMapping(PropertyToTimestampColumnMapping.builder())//
                .notTimestampBehaviour(ConvertableMappingErrorBehaviour.ABORT);
    }

    static Stream<Arguments> getNonTimestampTypes() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("test")), //
                Arguments.of(new BigDecimalHolderNode(BigDecimal.ONE)), //
                Arguments.of(new DoubleHolderNode(1.0)), //
                Arguments.of(new ObjectHolderNode(Collections.emptyMap())), //
                Arguments.of(new ArrayHolderNode(Collections.emptyList())), //
                Arguments.of(new BinaryHolderNode(new byte[] {})), //
                Arguments.of(new BooleanHolderNode(true)), //
                Arguments.of(new DateHolderNode(new Date(1234567))) //
        );
    }

    static Stream<Arguments> convertCases() {
        return Stream.of(//
                Arguments.of(new DateHolderNode(new Date(12345678L))), //
                Arguments.of(new DoubleHolderNode(12345678L)), //
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("12345678"))));
    }

    @Test
    void testConvertTimestamp() {
        final Timestamp timestamp = new Timestamp(123456789L);
        final TimestampHolderNode numberNode = new TimestampHolderNode(timestamp);
        final Object result = new PropertyToTimestampColumnValueExtractor(commonMappingBuilder().build())
                .mapValue(numberNode);
        assertThat(result, equalTo(timestamp));
    }

    @ParameterizedTest
    @MethodSource("getNonTimestampTypes")
    void testNonNumericsThrowException(final DocumentNode nonNumericNode) {
        final PropertyToTimestampColumnValueExtractor valueExtractor = new PropertyToTimestampColumnValueExtractor(
                commonMappingBuilder().notTimestampBehaviour(ConvertableMappingErrorBehaviour.ABORT).build());
        final Exception exception = assertThrows(ColumnValueExtractorException.class,
                () -> valueExtractor.mapValue(nonNumericNode));
        assertThat(exception.getMessage(), startsWith("E-VSD-80"));
    }

    @ParameterizedTest
    @MethodSource("getNonTimestampTypes")
    void testNonNumericsConvertsToNull(final DocumentNode nonNumericNode) {
        final PropertyToTimestampColumnValueExtractor valueExtractor = new PropertyToTimestampColumnValueExtractor(
                commonMappingBuilder().notTimestampBehaviour(ConvertableMappingErrorBehaviour.NULL).build());
        final Object result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT" })
    void testNullIsAlwaysConvertedToNull(final ConvertableMappingErrorBehaviour behaviour) {
        final PropertyToTimestampColumnValueExtractor valueExtractor = new PropertyToTimestampColumnValueExtractor(
                commonMappingBuilder().notTimestampBehaviour(behaviour).build());
        final Object result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("convertCases")
    void testConvertNonNumeric(final DocumentNode nonNumericNode) {
        final PropertyToTimestampColumnValueExtractor valueExtractor = new PropertyToTimestampColumnValueExtractor(
                commonMappingBuilder().notTimestampBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_NULL).build());
        final Object result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, equalTo(new Timestamp(12345678L)));
    }
}
