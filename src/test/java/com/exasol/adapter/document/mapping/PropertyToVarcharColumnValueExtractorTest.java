package com.exasol.adapter.document.mapping;

import static com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour.*;
import static com.exasol.adapter.document.edml.TruncateableMappingErrorBehaviour.TRUNCATE;
import static com.exasol.adapter.document.mapping.PropertyToColumnMappingBuilderQuickAccess.configureExampleMapping;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.exasol.adapter.document.documentnode.DocumentNode;
import com.exasol.adapter.document.documentnode.holder.*;
import com.exasol.adapter.document.edml.ConvertableMappingErrorBehaviour;
import com.exasol.adapter.document.edml.TruncateableMappingErrorBehaviour;

class PropertyToVarcharColumnValueExtractorTest {
    private static final String TEST_STRING = "test";

    private static PropertyToVarcharColumnMapping.Builder<?, ?> getDefaultMappingBuilder() {
        return configureExampleMapping(PropertyToVarcharColumnMapping.builder())//
                .varcharColumnSize(TEST_STRING.length())//
                .overflowBehaviour(TruncateableMappingErrorBehaviour.ABORT).nonStringBehaviour(CONVERT_OR_ABORT);
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
        final Object result = new PropertyToVarcharColumnValueExtractor(column).mapValue(new StringHolderNode("test"));
        assertThat(result, equalTo("test"));
    }

    @Test
    void testWithStringOverflowTruncate() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().overflowBehaviour(TRUNCATE)
                .varcharColumnSize(2).build();
        final Object result = new PropertyToVarcharColumnValueExtractor(column).mapValue(new StringHolderNode("test"));
        assertThat(result, equalTo("te"));
    }

    @Test
    void testWithStringOverflowNull() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder()
                .overflowBehaviour(TruncateableMappingErrorBehaviour.NULL).varcharColumnSize(2).build();
        final Object result = new PropertyToVarcharColumnValueExtractor(column).mapValue(new StringHolderNode("test"));
        assertThat(result, is(nullValue()));
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

    static Stream<Arguments> toStringConversionTestCases() {
        return Stream.of(//
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("123")), "CONVERT_OR_NULL", "123"), //
                Arguments.of(new BooleanHolderNode(true), "CONVERT_OR_NULL", "true"), //
                Arguments.of(new BinaryHolderNode("abc".getBytes()), "CONVERT_OR_NULL", "YWJj"), //
                Arguments.of(new BigDecimalHolderNode(new BigDecimal("123")), "CONVERT_OR_ABORT", "123"), //
                Arguments.of(new BooleanHolderNode(true), "CONVERT_OR_ABORT", "true"), //
                Arguments.of(new DoubleHolderNode(12.2), "CONVERT_OR_NULL", "12.2"), //
                Arguments.of(new DateHolderNode(new Date(1632212318000L)), "CONVERT_OR_NULL", "2021-09-21"), //
                Arguments.of(new TimestampHolderNode(new Timestamp(1632212318000L)), "CONVERT_OR_NULL",
                        "2021-09-21T08:18:38Z") //
        );
    }

    @ParameterizedTest
    @MethodSource("toStringConversionTestCases")
    void testToStringConversion(final DocumentNode nonStringNode, final ConvertableMappingErrorBehaviour behaviour,
            final String expectedResult) {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().nonStringBehaviour(behaviour)
                .varcharColumnSize(20).build();
        final Object result = new PropertyToVarcharColumnValueExtractor(column).mapValue(nonStringNode);
        assertThat(result, equalTo(expectedResult));
    }

    @Test
    void testConversionPossibleButAbort() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().nonStringBehaviour(ABORT).build();
        final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(column);
        final BooleanHolderNode documentValue = new BooleanHolderNode(true);
        final ColumnValueExtractorException columnValueExtractorException = assertThrows(
                ColumnValueExtractorException.class, () -> valueExtractor.mapValue(documentValue));
        assertThat(columnValueExtractorException.getMessage(), startsWith("E-VSD-36"));
    }

    @Test
    void testConversionPossibleButNull() {
        final PropertyToVarcharColumnMapping column = getDefaultMappingBuilder().nonStringBehaviour(NULL).build();
        final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(column);
        final Object result = valueExtractor.mapValue(new BooleanHolderNode(true));
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @CsvSource({ "NULL", "ABORT", "CONVERT_OR_NULL", "CONVERT_OR_ABORT" })
    void testNullIsAlwaysConvertedToNull(final ConvertableMappingErrorBehaviour behaviour) {
        final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(
                getDefaultMappingBuilder().nonStringBehaviour(behaviour).build());
        final Object result = valueExtractor.mapValue(new NullHolderNode());
        assertThat(result, is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("nonConvertibles")
    void testNonConvertiblesAreConvertedToNull(final DocumentNode nonConvertibleNode) {
        for (final var behaviour : Set.of(NULL, CONVERT_OR_NULL)) {
            final PropertyToVarcharColumnValueExtractor valueExtractor = new PropertyToVarcharColumnValueExtractor(
                    getDefaultMappingBuilder().nonStringBehaviour(behaviour).build());
            final Object result = valueExtractor.mapValue(nonConvertibleNode);
            assertThat(result, is(nullValue()));
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
            assertThat(columnValueExtractorException.getMessage(), startsWith("E-VSD-36"));
        }
    }
}
