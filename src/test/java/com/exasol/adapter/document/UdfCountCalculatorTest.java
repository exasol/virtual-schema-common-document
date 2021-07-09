package com.exasol.adapter.document;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exasol.ExaMetadata;

class UdfCountCalculatorTest {
    private static final BigInteger GB = new BigInteger("1000000000");

    @ParameterizedTest
    @CsvSource({ //
            "2, 2, 20, 100, 8", // limited by memory (2Gb / 500MB = 4 * 2 nodes = 8)
            "4, 2, 20, 100, 16", // limited by memory (4Gb / 500MB = 8 * 2 nodes = 16)
            "20, 2, 10, 100, 20", // limited by cores (10 cores * 2 nodes = 20)
            "20, 2, 10, 5, 5",// limited by config
    })
    void test(final int gbRam, final long nodes, final int coresPerNode, final int configLimit,
            final int expectedResult) {
        final ExaMetadata exaMetadata = mock(ExaMetadata.class);
        when(exaMetadata.getMemoryLimit()).thenReturn(GB.multiply(BigInteger.valueOf(gbRam)));
        when(exaMetadata.getNodeCount()).thenReturn(nodes);
        final DocumentAdapterProperties adapterProperties = mock(DocumentAdapterProperties.class);
        when(adapterProperties.getMaxParallelUdfs()).thenReturn(configLimit);
        final int result = new UdfCountCalculator().calculateMaxUdfInstanceCount(exaMetadata, adapterProperties,
                coresPerNode);
        assertThat(result, equalTo(expectedResult));
    }
}