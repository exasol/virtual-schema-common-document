package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.MappingErrorBehaviour.ABORT;
import static com.exasol.adapter.document.edml.MappingErrorBehaviour.NULL;
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

class PropertyToDecimalColumnValueExtractorTest {

    private static PropertyToDecimalColumnMapping.Builder<?, ?> commonMappingBuilder() {
        return configureExampleMapping(PropertyToDecimalColumnMapping.builder())//
                .overflowBehaviour(ABORT)//
                .notNumericBehaviour(ConvertableMappingErrorBehaviour.ABORT)//
                .decimalPrecision(8)//
                .decimalScale(3);
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

    @Test
    void testConvertDouble() {
        final DocumentFloatingPointValue numberNode = new DoubleHolderNode(1.23);
        final Object result = new PropertyToDecimalColumnValueExtractor(commonMappingBuilder().build())
                .mapValue(numberNode);
        assertThat(result, equalTo(new BigDecimal("1.230")));
    }

    @Test
    void testConvertBigDecimal() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(BigDecimal.valueOf(1.23));
        final Object result = new PropertyToDecimalColumnValueExtractor(commonMappingBuilder().build())
                .mapValue(numberNode);
        assertThat(result, equalTo(new BigDecimal("1.230")));
    }

    @Test
    void testConvertBigDecimalWithScaleReduction() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(BigDecimal.valueOf(1.23));
        final PropertyToDecimalColumnMapping column = commonMappingBuilder().decimalScale(0).build();
        final Object result = new PropertyToDecimalColumnValueExtractor(column).mapValue(numberNode);
        assertThat(result, equalTo(new BigDecimal("1")));
    }

    @Test
    void testConvertBigDecimalWithOnlyTypeOverflow() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(new BigDecimal("12"));
        final PropertyToDecimalColumnMapping column = commonMappingBuilder().decimalScale(0).decimalPrecision(2)
                .build();
        final Object result = new PropertyToDecimalColumnValueExtractor(column).mapValue(numberNode);
        assertThat(result, equalTo(new BigDecimal("12")));
    }

    @Test
    void testConvertBigDecimalWithPrecisionOverflow() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(new BigDecimal("12"));
        final PropertyToDecimalColumnMapping column = commonMappingBuilder().decimalScale(0).decimalPrecision(1)
                .build();
        final PropertyToDecimalColumnValueExtractor valueExtractor = new PropertyToDecimalColumnValueExtractor(column);
        final OverflowException overflowException = assertThrows(OverflowException.class,
                () -> valueExtractor.mapValue(numberNode));
        assertThat(overflowException.getMessage(), startsWith("E-VSD-34"));
    }

    @Test
    void testConvertBigDecimalWithPrecisionOverflowWithNullBehaviour() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(new BigDecimal("12"));
        final PropertyToDecimalColumnMapping column = commonMappingBuilder().decimalScale(0).decimalPrecision(1)
                .overflowBehaviour(NULL).build();
        final Object result = new PropertyToDecimalColumnValueExtractor(column).mapValue(numberNode);
        assertThat(result, is(nullValue()));
    }

    static Stream<Arguments> convertNonNumericCases() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("123"), 123), //
                Arguments.of(new StringHolderNode("12.3"), 12.3), //
                Arguments.of(new BooleanHolderNode(true), 1), //
                Arguments.of(new BooleanHolderNode(false), 0), //
                Arguments.of(new DateHolderNode(new Date(12345)), 12345), //
                Arguments.of(new TimestampHolderNode(new Timestamp(12345)), 12345) //
        );
    }

    @ParameterizedTest
    @MethodSource("getNonNumericTypes")
    void testNonNumericsThrowException(final DocumentNode nonNumericNode) {
        final PropertyToDecimalColumnValueExtractor valueExtractor = new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(ConvertableMappingErrorBehaviour.ABORT).build());
        final Exception exception = assertThrows(ColumnValueExtractorException.class,
                () -> valueExtractor.mapValue(nonNumericNode));
        assertThat(exception.getMessage(), startsWith("E-VSD-33"));
    }

    @ParameterizedTest
    @MethodSource("getNonNumericTypes")
    void testNonNumericsConvertsToNull(final DocumentNode nonNumericNode) {
        final PropertyToDecimalColumnValueExtractor valueExtractor = new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(ConvertableMappingErrorBehaviour.NULL).build());
        final Object result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT" })
    void testNullIsAlwaysConvertedToNull(final ConvertableMappingErrorBehaviour behaviour) {
        final PropertyToDecimalColumnValueExtractor valueExtractor = new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(behaviour).build());
        final Object result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("convertNonNumericCases")
    void testConvertNonNumeric(final DocumentNode nonNumericNode, final double expectedResult) {
        final PropertyToDecimalColumnValueExtractor valueExtractor = new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_NULL).build());
        final BigDecimal result = (BigDecimal) valueExtractor.mapValue(nonNumericNode);
        assertThat(result.doubleValue(), equalTo(expectedResult));
    }
}
