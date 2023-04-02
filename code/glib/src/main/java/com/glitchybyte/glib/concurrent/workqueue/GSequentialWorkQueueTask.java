// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.workqueue;

import java.util.function.Consumer;

/**
 * A task implementation of a {code GAsyncWorkQueue} that processes work items
 * sequentially.
 *
 * @param <T> Type of work item.
 */
public final class GSequentialWorkQueueTask<T> extends GWorkQueueTask<T> {

    /**
     * Creates a sequential work queue task.
     *
     * @param threadName Thread name.
     * @param processor Work item processor.
     */
    public GSequentialWorkQueueTask(final String threadName, final Consumer<T> processor) {
        super(threadName, processor);
    }

    @Override
    protected void processWork() {
        workQueue.doWork(processor);
    }
}
