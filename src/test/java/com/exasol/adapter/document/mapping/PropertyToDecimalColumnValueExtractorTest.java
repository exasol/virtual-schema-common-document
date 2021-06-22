package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.MappingErrorBehaviour.ABORT;
import static com.exasol.adapter.document.mapping.MappingErrorBehaviour.NULL;
import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.exasol.adapter.document.documentnode.DocumentFloatingPointValue;
import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.BigDecimalLiteral;
import com.exasol.sql.expression.literal.NullLiteral;

class PropertyToDecimalColumnValueExtractorTest {

    private static PropertyToDecimalColumnMapping.Builder commonMappingBuilder() {
        return configureExampleMapping(PropertyToDecimalColumnMapping.builder())//
                .overflowBehaviour(ABORT)//
                .notNumericBehaviour(ABORT)//
                .decimalPrecision(8)//
                .decimalScale(3);
    }

    static Stream<Arguments> getNonNumericTypes() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("test")), //
                Arguments.of(new BooleanHolderNode(true)), //
                Arguments.of(new ObjectHolderNode(Collections.emptyMap())), //
                Arguments.of(new ArrayHolderNode(Collections.emptyList()))//
        );
    }

    @Test
    void testConvertDouble() {
        final DocumentFloatingPointValue numberNode = new DoubleHolderNode(1.23);
        final BigDecimalLiteral result = (BigDecimalLiteral) new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().build()).mapValue(numberNode);
        assertThat(result.getValue(), equalTo(new BigDecimal("1.230")));
    }

    @Test
    void testConvertBigDecimal() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(BigDecimal.valueOf(1.23));
        final BigDecimalLiteral result = (BigDecimalLiteral) new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().build()).mapValue(numberNode);
        assertThat(result.getValue(), equalTo(new BigDecimal("1.230")));
    }

    @Test
    void testConvertBigDecimalWithScaleReduction() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(BigDecimal.valueOf(1.23));
        final PropertyToDecimalColumnMapping column = commonMappingBuilder().decimalScale(0).build();
        final BigDecimalLiteral result = (BigDecimalLiteral) new PropertyToDecimalColumnValueExtractor(column)
                .mapValue(numberNode);
        assertThat(result.getValue(), equalTo(new BigDecimal("1")));
    }

    @Test
    void testConvertBigDecimalWithOnlyTypeOverflow() {
        final BigDecimalHolderNode numberNode = new BigDecimalHolderNode(new BigDecimal("12"));
        final PropertyToDecimalColumnMapping column = commonMappingBuilder().decimalScale(0).decimalPrecision(2)
                .build();
        final BigDecimalLiteral result = (BigDecimalLiteral) new PropertyToDecimalColumnValueExtractor(column)
                .mapValue(numberNode);
        assertThat(result.getValue(), equalTo(new BigDecimal("12")));
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
        final ValueExpression result = new PropertyToDecimalColumnValueExtractor(column).mapValue(numberNode);
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @ParameterizedTest
    @MethodSource("getNonNumericTypes")
    void testNonNumericsThrowException(final DocumentNode nonNumericNode) {
        final PropertyToDecimalColumnValueExtractor valueExtractor = new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(ABORT).build());
        final Exception exception = assertThrows(ColumnValueExtractorException.class,
                () -> valueExtractor.mapValue(nonNumericNode));
        assertThat(exception.getMessage(), startsWith("E-VSD-33"));
    }

    @ParameterizedTest
    @MethodSource("getNonNumericTypes")
    void testNonNumericsConvertsToNull(final DocumentNode nonNumericNode) {
        final PropertyToDecimalColumnValueExtractor valueExtractor = new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(NULL).build());
        final ValueExpression result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT" })
    void testNullIsAlwaysConvertedToNull(final MappingErrorBehaviour behaviour) {
        final PropertyToDecimalColumnValueExtractor valueExtractor = new PropertyToDecimalColumnValueExtractor(
                commonMappingBuilder().notNumericBehaviour(behaviour).build());
        final ValueExpression result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, instanceOf(NullLiteral.class));
    }
}