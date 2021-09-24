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
import com.exasol.sql.expression.ValueExpression;
import com.exasol.sql.expression.literal.DateLiteral;
import com.exasol.sql.expression.literal.NullLiteral;

class PropertyToDateColumnValueExtractorTest {
    private static PropertyToDateColumnMapping.PropertyToDateColumnMappingBuilder<?, ?> commonMappingBuilder() {
        return configureExampleMapping(PropertyToDateColumnMapping.builder())//
                .notDateBehaviour(ConvertableMappingErrorBehaviour.ABORT);
    }

    static Stream<Arguments> getNonDateTypes() {
        return Stream.of(//
                Arguments.of(new StringHolderNode("test")), //
                Arguments.of(new BigDecimalHolderNode(BigDecimal.ONE)), //
                Arguments.of(new DoubleHolderNode(1.0)), //
                Arguments.of(new ObjectHolderNode(Collections.emptyMap())), //
                Arguments.of(new ArrayHolderNode(Collections.emptyList())), //
                Arguments.of(new BinaryHolderNode(new byte[] {})), //
                Arguments.of(new BooleanHolderNode(true)), //
                Arguments.of(new TimestampHolderNode(new Timestamp(1234567))) //
        );
    }

    static Stream<Arguments> convertCases() {
        return Stream.of(//
                Arguments.of(new TimestampHolderNode(new Timestamp(12345678L))), //
                Arguments.of(new DoubleHolderNode(12345678L)), //
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("12345678"))));
    }

    @Test
    void testConvertDate() {
        final Date date = new Date(123456789L);
        final DateHolderNode numberNode = new DateHolderNode(date);
        final DateLiteral result = (DateLiteral) new PropertyToDateColumnValueExtractor(commonMappingBuilder().build())
                .mapValue(numberNode);
        assertThat(result.toDate(), equalTo(date));
    }

    @ParameterizedTest
    @MethodSource("getNonDateTypes")
    void testNonNumericsThrowException(final DocumentNode nonNumericNode) {
        final PropertyToDateColumnValueExtractor valueExtractor = new PropertyToDateColumnValueExtractor(
                commonMappingBuilder().notDateBehaviour(ConvertableMappingErrorBehaviour.ABORT).build());
        final Exception exception = assertThrows(ColumnValueExtractorException.class,
                () -> valueExtractor.mapValue(nonNumericNode));
        assertThat(exception.getMessage(), startsWith("E-VSD-79"));
    }

    @ParameterizedTest
    @MethodSource("getNonDateTypes")
    void testNonNumericsConvertsToNull(final DocumentNode nonNumericNode) {
        final PropertyToDateColumnValueExtractor valueExtractor = new PropertyToDateColumnValueExtractor(
                commonMappingBuilder().notDateBehaviour(ConvertableMappingErrorBehaviour.NULL).build());
        final ValueExpression result = valueExtractor.mapValue(nonNumericNode);
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT" })
    void testNullIsAlwaysConvertedToNull(final ConvertableMappingErrorBehaviour behaviour) {
        final PropertyToDateColumnValueExtractor valueExtractor = new PropertyToDateColumnValueExtractor(
                commonMappingBuilder().notDateBehaviour(behaviour).build());
        final ValueExpression result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @ParameterizedTest
    @MethodSource("convertCases")
    void testConvertNonNumeric(final DocumentNode nonNumericNode) {
        final PropertyToDateColumnValueExtractor valueExtractor = new PropertyToDateColumnValueExtractor(
                commonMappingBuilder().notDateBehaviour(ConvertableMappingErrorBehaviour.CONVERT_OR_NULL).build());
        final DateLiteral result = (DateLiteral) valueExtractor.mapValue(nonNumericNode);
        assertThat(result.toDate(), equalTo(new Timestamp(12345678L)));
    }
}