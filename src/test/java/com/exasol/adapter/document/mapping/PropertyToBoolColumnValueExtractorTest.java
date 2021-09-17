package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.BooleanLiteral;
import com.exasol.sql.expression.literal.NullLiteral;

class PropertyToBoolColumnValueExtractorTest {
    private static PropertyToBoolColumnMapping.PropertyToBoolColumnMappingBuilder<?, ?> commonMappingBuilder() {
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
                Arguments.of(new BinaryHolderNode(new byte[] {}))//
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
        final BooleanLiteral result = (BooleanLiteral) new PropertyToBoolColumnValueExtractor(
                commonMappingBuilder().build()).mapValue(numberNode);
        assertThat(result.toBoolean(), equalTo(boolValue));
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
        final ValueExpression result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT" })
    void testNullIsAlwaysConvertedToNull(final ConvertableMappingErrorBehaviour behaviour) {
        final PropertyToBoolColumnValueExtractor valueExtractor = new PropertyToBoolColumnValueExtractor(
                commonMappingBuilder().notBooleanBehavior(behaviour).build());
        final ValueExpression result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @ParameterizedTest
    @MethodSource("convertNonNumericCases")
    void testConvertNonNumeric(final DocumentNode nonNumericNode, final boolean expectedResult) {
        final PropertyToBoolColumnValueExtractor valueExtractor = new PropertyToBoolColumnValueExtractor(
                commonMappingBuilder().notBooleanBehavior(ConvertableMappingErrorBehaviour.CONVERT_OR_NULL).build());
        final BooleanLiteral result = (BooleanLiteral) valueExtractor.mapValue(nonNumericNode);
        assertThat(result.toBoolean(), equalTo(expectedResult));
    }
}