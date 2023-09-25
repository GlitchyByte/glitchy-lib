// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import java.util.concurrent.ExecutorService;

/**
 * Abstract class for task runner facilities.
 *
 * @param <ES> An {@link ExecutorService} or descendant.
 */
public sealed abstract class GTaskExecutor<ES extends ExecutorService> implements AutoCloseable
        permits GTaskRunner, GTaskScheduler {

    /**
     * Actual {@link ExecutorService} runner.
     */
    protected final ES runner;

    /**
     * Creates a task runner with the given {@link ExecutorService}.
     *
     * <p>This runner owns the given {@link ExecutorService} and will shut it
     * down and close it when the runner is closed.
     *
     * @param runner {@link ExecutorService} to use as runner.
     */
    public GTaskExecutor(final ES runner) {
        this.runner = runner;
    }

    @Override
    public void close() {
        runner.shutdownNow();
        runner.close();
    }

    /**
     * Creates a wrapper around the given {@link GTask} that sets the thread
     * name, if given, and marks task as done when it's done.
     *
     * @param task {@link GTask} to wrap.
     * @return A wrapped {@link GTask} in a {@link Runnable}.
     */
    protected Runnable createTaskWrapper(final GTask task) {
        return () -> {
            final String threadName = task.getTaskThreadName();
            if (threadName != null) {
                Thread.currentThread().setName(threadName);
            }
            task.run();
            task.done();
        };
    }
}
