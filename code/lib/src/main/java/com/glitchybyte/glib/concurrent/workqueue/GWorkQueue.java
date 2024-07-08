// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.workqueue;

import java.util.function.Consumer;

/**
 * A work queue system. Very low contention queueing of work. Options to
 * process work sequentially or in parallel. Each work item processing happens
 * on its own thread, giving the implementor freedom to do heavy lifting code
 * directly on the processor function without devising other mechanisms to free
 * the thread fast.
 *
 * @param <T> Work item type.
 */
public interface GWorkQueue<T> {

    /**
     * GWorkQueue builder.
     *
     * @param <T> Work item type.
     */
    class Builder<T> {

        private final String threadName;
        private final Consumer<T> processor;
        private final boolean parallelProcessing;

        /**
         * Creates a {@code GWorkQueueTask} builder.
         *
         * @param threadName Thread name.
         * @param processor Work item processor.
         * @param parallelProcessing True, if work items should be processed in parallel.
         */
        public Builder(final String threadName, final Consumer<T> processor, final boolean parallelProcessing) {
            this.threadName = threadName;
            this.processor = processor;
            this.parallelProcessing = parallelProcessing;
        }

        /**
         * Creates a GWorkQueue builder with default values.
         */
        public Builder() {
            this(null, null, false);
        }

        /**
         * Changes the thread name.
         *
         * @param threadName Thread name.
         * @return A new builder with updated values.
         */
        public Builder<T> withThreadName(final String threadName) {
            return new Builder<>(threadName, processor, parallelProcessing);
        }

        /**
         * Changes the work item processor.
         *
         * @param processor Work item processor.
         * @return A new builder with updated values.
         */
        public Builder<T> withProcessor(final Consumer<T> processor) {
            return new Builder<>(threadName, processor, parallelProcessing);
        }

        /**
         * Changes if work items should be processed in parallel or sequentially.
         *
         * @param parallelProcessing True, if work items should be processed in parallel.
         * @return A new builder with updated values.
         */
        public Builder<T> withParallelProcessing(final boolean parallelProcessing) {
            return new Builder<>(threadName, processor, parallelProcessing);
        }

        /**
         * Builds a {@code GWorkQueueTask} ready to be started.
         *
         * @return A {@code GWorkQueueTask} ready to be started.
         */
        public GWorkQueueTask<T> build() {
            return parallelProcessing ?
                    new GParallelWorkQueueTask<>(threadName, processor) :
                    new GSequentialWorkQueueTask<>(threadName, processor);
        }
    }

    /**
     * Adds work to the work queue.
     *
     * @param item Work item to add.
     */
    void addWork(final T item);
}
