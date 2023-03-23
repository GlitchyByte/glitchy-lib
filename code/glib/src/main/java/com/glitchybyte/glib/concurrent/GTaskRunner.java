// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A task runner facility to run {@code GConcurrentTask} tasks.
 */
public final class GTaskRunner implements AutoCloseable {

    private final ExecutorService runner;

    /**
     * Creates a task runner with the given {@code ExecutorService}.
     *
     * @param runner {@code ExecutorService} to use as runner.
     */
    public GTaskRunner(final ExecutorService runner) {
        this.runner = runner;
    }

    /**
     * Creates a task runner with a default runner.
     */
    public GTaskRunner() {
        this(Executors.newCachedThreadPool());
    }

    /**
     * Submits a task to be run concurrently.
     *
     * <p>If the task does not start within the given timeout, an
     * {@code IllegalStateException} will be raised as this is considered a
     * bug in the task implementation.
     *
     * <p>This method blocks until the task has started.
     *
     * @param task Task to start.
     * @param timeout Time given to the task to start.
     * @return The started task.
     * @param <T> Task type.
     * @throws InterruptedException If the thread is interrupted while waiting for the task to start.
     */
    public <T extends GConcurrentTask> T start(final T task, final Duration timeout) throws InterruptedException {
        runner.submit(task);
        task.awaitStarted(timeout);
        return task;
    }

    /**
     * Submits a task to be run concurrently.
     *
     * <p>If the task does not start within 5 seconds, an
     * {@code IllegalStateException} will be raised as this is considered a
     * bug in the task implementation.
     *
     * <p>This method blocks until the task has started.
     *
     * @param task Task to start.
     * @return The started task.
     * @param <T> Task type.
     * @throws InterruptedException If the thread is interrupted while waiting for the task to start.
     */
    public <T extends GConcurrentTask> T start(final T task) throws InterruptedException {
        return start(task, Duration.ofSeconds(5));
    }

    /**
     * Submits a {@code Runnable} to be run concurrently.
     *
     * <p>This is a convenience method to reuse this runner on an arbitrary
     * {@code Runnable} task. It will mark the task as started when the thread
     * is already running, but before executing the {@code Runnable} code.
     *
     * @param task A {@code Runnable} to run.
     */
    public void run(final Runnable task) {
        final GConcurrentTask wrapperTask = new GConcurrentTask() {
            @Override
            public void run() {
                started();
                task.run();
            }
        };
        try {
            start(wrapperTask);
        } catch (final InterruptedException e) {
            // This can't happen because we are marking as started ourselves.
        }
    }

    @Override
    public void close() {
        runner.shutdownNow();
        runner.close();
    }
}
