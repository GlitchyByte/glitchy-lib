// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.workqueue;

import java.util.function.Consumer;

/**
 * A task implementation of a {code GAsyncWorkQueue} that processes work items
 * in parallel.
 *
 * @param <T> Type of work item.
 */
public final class GParallelWorkQueueTask<T> extends GWorkQueueTask<T> {

    /**
     * Creates a parallel work queue task.
     *
     * @param threadName Thread name.
     * @param processor Work item processor.
     */
    public GParallelWorkQueueTask(final String threadName, final Consumer<T> processor) {
        super(threadName, processor);
    }

    @Override
    protected void processWork() {
        workQueue.doWork(item -> {
            getTaskRunner().run(() -> processor.accept(item));
        });
    }
}
