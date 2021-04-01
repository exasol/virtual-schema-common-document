package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.mapping.ConvertableMappingErrorBehaviour.*;
import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static com.exasol.adapter.document.mapping.TruncateableMappingErrorBehaviour.TRUNCATE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.sql.expression.*;

class PropertyToVarcharColumnValueExtractorTest {
    private static final String TEST_STRING = "test";

    private static PropertyToVarcharColumnMapping.Builder getDefaultMappingBuilder() {
        return configureExampleMapping(PropertyToVarcharColumnMapping.builder())//
                .varcharColumnSize(TEST_STRING.length())//
                .overflowBehaviour(TruncateableMappingErrorBehaviour.ABORT).nonStringBehaviour(CONVERT_OR_ABORT);
    }

    static Stream<Arguments> toStringConversionTestCases() {
        return Stream.of(//
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("123")), "CONVERT_OR_NULL", "123"), //
                Arguments.of(new BooleanHolderNode(true), "CONVERT_OR_NULL", "true"), //
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("123")), "CONVERT_OR_ABORT", "123"), //
                Arguments.of(new BooleanHolderNode(true), "CONVERT_OR_ABORT", "true")//
        );
    }

    static Stream<Arguments> nonConvertibles() {
        return Stream.of(//
                Arguments.of(new ArrayHolderNode(Collections.emptyList())), //
                Arguments.of(new ObjectHolderNode(Collections.emptyMap()))//
        );
    }

    @Test
    void testWithString() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().build();
        final StringLiteral result = (StringLiteral) new PropertyToVarcharColumnValueExtractor(column)
                .mapValue(new StringHolderNode("test"));
        assertThat(result.toString(), equalTo("test"));
    }

    @Test
    void testWithStringOverflowTruncate() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().overflowBehaviour(TRUNCATE)
                .varcharColumnSize(2).build();
        final StringLiteral result = (StringLiteral) new PropertyToVarcharColumnValueExtractor(column)
                .mapValue(new StringHolderNode("test"));
        assertThat(result.toString(), equalTo("te"));
    }

    @Test
    void testWithStringOverflowNull() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder()
                .overflowBehaviour(TruncateableMappingErrorBehaviour.NULL).varcharColumnSize(2).build();
        final ValueExpression result = new PropertyToVarcharColumnValueExtractor(column)
                .mapValue(new StringHolderNode("test"));
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @Test
    void testWithStringOverflowAbort() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder()
                .overflowBehaviour(TruncateableMappingErrorBehaviour.ABORT).varcharColumnSize(2).build();
        final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(column);
        final StringHolderNode testValue = new StringHolderNode("test");
        final OverflowException overflowException = assertThrows(OverflowException.class,
                () -> valueExtractor.mapValue(testValue));
        assertThat(overflowException.getMessage(), startsWith("E-VSD-38"));
    }

    @ParameterizedTest
    @MethodSource("toStringConversionTestCases")
    void testToStringConversion(final DocumentNode nonStringNode, final ConvertableMappingErrorBehaviour behaviour,
            final String expectedResult) {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().nonStringBehaviour(behaviour).build();
        final StringLiteral result = (StringLiteral) new PropertyToVarcharColumnValueExtractor(column)
                .mapValue(nonStringNode);
        assertThat(result.toString(), equalTo(expectedResult));
    }

    @Test
    void testConversionPossibleButAbort() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().nonStringBehaviour(ABORT).build();
        final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(column);
        final ColumnValueExtractorException columnValueExtractorException = assertThrows(
                ColumnValueExtractorException.class, () -> valueExtractor.mapValue(new BooleanHolderNode(true)));
        assertThat(columnValueExtractorException.getMessage(), startsWith("E-VSD-36"));
    }

    @Test
    void testConversionPossibleButNull() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().nonStringBehaviour(NULL).build();
        final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(column);
        final ValueExpression result = valueExtractor.mapValue(new BooleanHolderNode(true));
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT", "CONVERT_OR_NULL", "CONVERT_OR_ABORT" })
    void testNullIsAlwaysConvertedToNull(final ConvertableMappingErrorBehaviour behaviour) {
        final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(
                getDefaultMappingBuilder().nonStringBehaviour(behaviour).build());
        final ValueExpression result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, instanceOf(NullLiteral.class));
    }

    @ParameterizedTest
    @MethodSource("nonConvertibles")
    void testNonConvertiblesAreConvertedToNull(final DocumentNode nonConvertibleNode) {
        for (final var behaviour : Set.of(NULL, CONVERT_OR_NULL)) {
            final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(
                    getDefaultMappingBuilder().nonStringBehaviour(NULL).build());
            final ValueExpression result = valueExtractor.mapValue(nonConvertibleNode);
            assertThat(result, instanceOf(NullLiteral.class));
        }
    }

    @ParameterizedTest
    @MethodSource("nonConvertibles")
    void testNonConvertiblesThrowException(final DocumentNode nonConvertibleNode) {
        for (final var behaviour : Set.of(ABORT, CONVERT_OR_ABORT)) {
            final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(
                    getDefaultMappingBuilder().nonStringBehaviour(behaviour).build());
            final ColumnValueExtractorException columnValueExtractorException = assertThrows(
                    ColumnValueExtractorException.class, () -> valueExtractor.mapValue(nonConvertibleNode));
            assertThat(columnValueExtractorException.getMessage(), startsWith("E-VSD-37"));
        }
    }
}
