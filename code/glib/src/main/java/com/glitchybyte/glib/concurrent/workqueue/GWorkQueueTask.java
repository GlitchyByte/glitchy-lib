// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.workqueue;

import com.glitchybyte.glib.concurrent.GConcurrentTask;

import java.util.function.Consumer;

/**
 * A base task implementation of a {code GAsyncWorkQueue}.
 *
 * @param <T> Type of work item.
 */
public abstract class GWorkQueueTask<T> extends GConcurrentTask implements GWorkQueue<T> {

    /**
     * Work queue.
     */
    protected final GAsyncWorkQueue<T> workQueue = new GAsyncWorkQueue<>();

    /**
     * Work item processor.
     */
    protected final Consumer<T> processor;

    /**
     * Creates a work queue task.
     *
     * @param threadName Thread name.
     * @param processor Work item processor.
     */
    public GWorkQueueTask(final String threadName, final Consumer<T> processor) {
        super(threadName);
        if (processor == null) {
            throw new IllegalArgumentException("Processor can't be null!");
        }
        this.processor = processor;
    }

    @Override
    public void addWork(final T item) {
        workQueue.addWork(item);
    }

    @Override
    public final void run() {
        started();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                workQueue.awaitWork();
                processWork();
            }
        } catch (final InterruptedException e) {
            // Exiting!
        }
    }

    /**
     * This method processes each work item.
     *
     * <p>Implementations MUST call {@code workQueue.doWork}.
     */
    protected abstract void processWork();
}
