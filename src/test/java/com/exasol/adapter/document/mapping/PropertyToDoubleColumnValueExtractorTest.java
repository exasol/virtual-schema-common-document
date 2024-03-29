package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.MappingErrorBehaviour.ABORT;
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

import com.exasol.adapter.document.documentnode.DocumentFloatingPointValue;
import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;

class PropertyToDoubleColumnValueExtractorTest {

    private static PropertyToDoubleColumnMapping.Builder<?, ?> commonMappingBuilder() {
        return configureExampleMapping(PropertyToDoubleColumnMapping.builder())//
                .overflowBehaviour(ABORT)//
                .notNumericBehaviour(ConvertableMappingErrorBehaviour.ABORT);
    }

    static Stream<Arguments> getNonNumericTypes() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("test")), //
                Arguments.of(new BooleanHolderNode(true)), //
                Arguments.of(new ObjectHolderNode(Collections.emptyMap())), //
                Arguments.of(new ArrayHolderNode(Collections.emptyList())), //
                Arguments.of(new BinaryHolderNode(new byte[] {})), //
                Arguments.of(new DateHolderNode(new Date(123))), //
                Arguments.of(new TimestampHolderNode(new Timestamp(123)))//
        );
    }

    static Stream<Arguments> nonDoubleValueSupplier() {
        return Stream.of(//
                Arguments.of(BigDecimal.valueOf(Double.MAX_VALUE).add(BigDecimal.valueOf(100))),
                Arguments.of(BigDecimal.valueOf(-Double.MAX_VALUE).subtract(BigDecimal.valueOf(100)))//
        );
    }

    static Stream<Arguments> convertNonNumericCases() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("123"), 123), //
                Arguments.of(new StringHolderNode("12.3"), 12.3), //
                Arguments.of(new BooleanHolderNode(true), 1), //
                Arguments.of(new BooleanHolderNode(false), 0) //
        );
    }

    @Test
    void testConvertDouble() {
        final DocumentFloatingPointValue numberNode = new DoubleHolderNode(1.23);
        final Object result = new PropertyToDoubleColumnValueExtractor(commonMappingBuilder().build())
                .mapValue(numberNode);
        assertThat(result, equalTo(1.23));
    }

    @Test
    void testConvertBigDecimal() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(BigDecimal.valueOf(1.23));
        final Object result = new PropertyToDoubleColumnValueExtractor(commonMappingBuilder().build())
                .mapValue(numberNode);
        assertThat(result, equalTo(1.23));
    }

    @ParameterizedTest
    @MethodSource("nonDoubleValueSupplier")
    void testConvertBigDecimalWithOverflow(final BigDecimal nonDoubleValue) {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(nonDoubleValue);
        final PropertyToDoubleColumnValueExtractor extractor = new PropertyToDoubleColumnValueExtractor(
                commonMappingBuilder().overflowBehaviour(ABORT).build());
        final OverflowException overflowException = assertThrows(OverflowException.class,
                () -> extractor.mapValue(numberNode));
        assertThat(overflowException.getMessage(), startsWith("E-VSD-77: The input value "));
    }

    @ParameterizedTest
    @MethodSource("getNonNumericTypes")
    void testNonNumericsThrowException(final DocumentNode nonNumericNode) {
        final PropertyToDoubleColumnValueExtractor valueExtractor = new PropertyToDoubleColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(ConvertableMappingErrorBehaviour.ABORT).build());
        final Exception exception = assertThrows(ColumnValueExtractorException.class,
                () -> valueExtractor.mapValue(nonNumericNode));
        assertThat(exception.getMessage(), startsWith("E-VSD-33"));
    }

    @ParameterizedTest
    @MethodSource("getNonNumericTypes")
    void testNonNumericsConvertsToNull(final DocumentNode nonNumericNode) {
        final PropertyToDoubleColumnValueExtractor valueExtractor = new PropertyToDoubleColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(ConvertableMappingErrorBehaviour.NULL).build());
        final Object result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT" })
    void testNullIsAlwaysConvertedToNull(final ConvertableMappingErrorBehaviour behaviour) {
        final PropertyToDoubleColumnValueExtractor valueExtractor = new PropertyToDoubleColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(behaviour).build());
        final Object result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("convertNonNumericCases")
    void testConvertNonNumeric(final DocumentNode nonNumericNode, final double expectedResult) {
        final PropertyToDoubleColumnValueExtractor valueExtractor = new PropertyToDoubleColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_NULL).build());
        final Object result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, equalTo(expectedResult));
    }
}
