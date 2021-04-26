package com.exasol.adapter.document;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class monitors the fill state of the buffers in the {@link DataProcessingPipeline}.
 */
class DataProcessingPipelineMonitor extends Thread {
    private static final Logger LOGGER = Logger.getLogger(DataProcessingPipelineMonitor.class.getName());
    private final AtomicBoolean stopRequested = new AtomicBoolean(false);
    long a = 0;
    long b = 0;
    long c = 0;
    long d = 0;

    /**
     * Called when a row is loaded into the first buffer.
     * 
     * @param valueToPass a value
     * @param <T>         type of the value
     * @return value
     */
    public <T> T onEnterPreSchemaMappingBuffer(final T valueToPass) {
        this.a++;
        return valueToPass;
    }

    /**
     * Called when an item is removed from the first buffer.
     */
    public void onLeavePreSchemaMappingBuffer() {
        this.b++;
    }

    /**
     * Called when an item is loaded into the first buffer.
     */
    public void onEnterPreEmitBuffer() {
        this.c++;
    }

    /**
     * Called when an item is removed from the second buffer.
     */
    public void onLeavePreEmitBuffer() {
        this.d++;
    }

    /**
     * Stop the monitor.
     */
    public void requestStop() {
        this.stopRequested.set(true);
        this.interrupt();
    }

    @Override
    public void run() {
        while (!this.stopRequested.get()) {
            LOGGER.log(Level.INFO, "pipeline state: {0} {1}", new Object[] { (this.a - this.b), (this.c - this.d) });
            try {
                Thread.sleep(500);
            } catch (final InterruptedException exception) {
                interrupt();
            }
        }
    }
}
