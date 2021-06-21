package com.exasol.adapter.document.documentnode.objectwrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.document.documentnode.*;
import com.exasol.adapter.document.documentnode.holder.BooleanHolderNode;

class ObjectWrapperDocumentNodeFactoryTest {

    private static Stream<Arguments> testTypeWrappingCases() {
        return Stream.of(//
                Arguments.of("test", DocumentStringValue.class), //
                Arguments.of(1, DocumentDecimalValue.class), //
                Arguments.of(1L, DocumentDecimalValue.class), //
                Arguments.of(BigDecimal.valueOf(124), DocumentDecimalValue.class), //
                Arguments.of((short) 1, DocumentDecimalValue.class), //
                Arguments.of(1.1f, DocumentFloatingPointValue.class), //
                Arguments.of(1.1d, DocumentFloatingPointValue.class), //
                Arguments.of(false, BooleanHolderNode.class), //
                Arguments.of(List.of(1, "test"), DocumentArray.class), //
                Arguments.of(Map.of("key", "test"), DocumentObject.class)//
        );
    }

    @MethodSource("testTypeWrappingCases")
    @ParameterizedTest
    void testTypeWrapping(final Object input, final Class<?> expectedType) {
        assertThat(ObjectWrapperDocumentNodeFactory.getNodeFor(input), instanceOf(expectedType));
    }
}