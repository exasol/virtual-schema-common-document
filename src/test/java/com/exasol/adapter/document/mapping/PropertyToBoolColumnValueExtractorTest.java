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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;

class PropertyToBoolColumnValueExtractorTest {
    private static PropertyToBoolColumnMapping.Builder<?, ?> commonMappingBuilder() {
        return configureExampleMapping(PropertyToBoolColumnMapping.builder())//
                .notBooleanBehavior(ConvertableMappingErrorBehaviour.ABORT);
    }

    static Stream<Arguments> getNonBooleanTypes() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("test")), //
                Arguments.of(new BigDecimalHolderNode(BigDecimal.ONE)), //
                Arguments.of(new DoubleHolderNode(1.0)), //
                Arguments.of(new ObjectHolderNode(Collections.emptyMap())), //
                Arguments.of(new ArrayHolderNode(Collections.emptyList())), //
                Arguments.of(new BinaryHolderNode(new byte[] {})), //
                Arguments.of(new DateHolderNode(new Date(123))), //
                Arguments.of(new TimestampHolderNode(new Timestamp(123)))//
        );
    }

    static Stream<Arguments> convertNonNumericCases() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("true"), true), //
                Arguments.of(new StringHolderNode("test"), false), //
                Arguments.of(new DoubleHolderNode(1.0), true), //
                Arguments.of(new DoubleHolderNode(10), true), //
                Arguments.of(new DoubleHolderNode(0), false), //
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("1")), true), //
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("10")), true), //
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("0")), false) //
        );
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testConvertBoolean(final boolean boolValue) {
        final BooleanHolderNode numberNode = new BooleanHolderNode(boolValue);
        final Object result = new PropertyToBoolColumnValueExtractor(commonMappingBuilder().build())
                .mapValue(numberNode);
        assertThat(result, equalTo(boolValue));
    }

    @ParameterizedTest
    @MethodSource("getNonBooleanTypes")
    void testNonNumericsThrowException(final DocumentNode nonNumericNode) {
        final PropertyToBoolColumnValueExtractor valueExtractor = new PropertyToBoolColumnValueExtractor(
                commonMappingBuilder().notBooleanBehavior(ConvertableMappingErrorBehaviour.ABORT).build());
        final Exception exception = assertThrows(ColumnValueExtractorException.class,
                () -> valueExtractor.mapValue(nonNumericNode));
        assertThat(exception.getMessage(), startsWith("E-VSD-78"));
    }

    @ParameterizedTest
    @MethodSource("getNonBooleanTypes")
    void testNonNumericsConvertsToNull(final DocumentNode nonNumericNode) {
        final PropertyToBoolColumnValueExtractor valueExtractor = new PropertyToBoolColumnValueExtractor(
                commonMappingBuilder().notBooleanBehavior(ConvertableMappingErrorBehaviour.NULL).build());
        final Object result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT" })
    void testNullIsAlwaysConvertedToNull(final ConvertableMappingErrorBehaviour behaviour) {
        final PropertyToBoolColumnValueExtractor valueExtractor = new PropertyToBoolColumnValueExtractor(
                commonMappingBuilder().notBooleanBehavior(behaviour).build());
        final Object result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("convertNonNumericCases")
    void testConvertNonNumeric(final DocumentNode nonNumericNode, final boolean expectedResult) {
        final PropertyToBoolColumnValueExtractor valueExtractor = new PropertyToBoolColumnValueExtractor(
                commonMappingBuilder().notBooleanBehavior(ConvertableMappingErrorBehaviour.CONVERT_OR_NULL).build());
        final Object result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, equalTo(expectedResult));
    }
}
