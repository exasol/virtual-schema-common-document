package com.exasol.adapter.document;

import java.math.BigInteger;
import java.util.logging.Logger;

import com.exasol.ExaMetadata;
import com.exasol.adapter.document.properties.DocumentAdapterProperties;

/**
 * This class calculates the maximum number of UDFs that can be used to solve a query.
 */
class UdfCountCalculator {
    private static final int MB = 1000000;
    /**
     * A JAVA-UDF needs about 150 MB RAM. The rest is an estimate for buffering / handling data.
     */
    private static final int UDF_MIN_MEMORY = 500 * MB;
    private static final Logger LOGGER = Logger.getLogger(UdfCountCalculator.class.getName());

    /**
     * Get the maximum number of UDFs that can be used to solve the query.
     *
     * @implNote This method assumes that all cluster nodes have an equal number of cores.
     *
     * @param exaMetadata               {@link ExaMetadata}
     * @param documentAdapterProperties adapter properties
     * @param coresPerNode              number of CPU cores per DB node
     * @return maximum number of UDFs to start
     */
    public int calculateMaxUdfInstanceCount(final ExaMetadata exaMetadata,
            final DocumentAdapterProperties documentAdapterProperties, final int coresPerNode) {
        final BigInteger memoryLimit = exaMetadata.getMemoryLimit();
        final int maximumNodesByMemory = memoryLimit.divide(BigInteger.valueOf(UDF_MIN_MEMORY)).intValue();
        final int nodeCount = (int) exaMetadata.getNodeCount();
        final int autoDetectedMaximum = nodeCount * Math.min(maximumNodesByMemory, coresPerNode);
        final int maxConfigured = documentAdapterProperties.getMaxParallelUdfs();
        final int result = Math.min(autoDetectedMaximum, maxConfigured);
        LOGGER.info(() -> String.format("Calculating maximum UDF number as min(\n" + //
                "    cores in cluster (%d),\n" + //
                "    (memory per node (%d MB) / min ram per udf (%d MB)) * node count (%d) ) (= %d)\n" + //
                "    configuration MAX_PARALLEL_UDFS (%d)\n" + //
                ") = %d\n"
                + "Note that this is just the maximum. The dialect will decide later how many UDFs will be started.",
                coresPerNode * nodeCount, memoryLimit.divide(BigInteger.valueOf(MB)).intValue(), UDF_MIN_MEMORY / MB,
                nodeCount, maximumNodesByMemory * nodeCount, maxConfigured, result));
        return result;
    }
}
