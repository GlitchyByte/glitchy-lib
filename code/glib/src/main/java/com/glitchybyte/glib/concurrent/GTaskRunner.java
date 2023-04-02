// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import com.glitchybyte.glib.GArrays;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A task runner facility to run {@code GConcurrentTask} tasks.
 *
 * <p>Classes ending in 'Task' in the concurrent package must be started with
 * a {@code GTaskRunner}.
 */
public final class GTaskRunner implements AutoCloseable {

    private final ExecutorService runner;
    private final Lock runnerLock = new ReentrantLock();

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
        task.setTaskRunner(this);
        run(task);
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
     * @param runnable A {@code Runnable} to run.
     */
    public void run(final Runnable runnable) {
        GLock.locked(runnerLock, () -> runner.execute(runnable));
    }

    /**
     * Submits an array of {@code Runnable} to be run concurrently.
     *
     * @param runnables An array of {@code Runnable} to be run concurrently.
     */
    public void runAll(final Runnable[] runnables) {
        GArrays.forEach(runnables, this::run);
    }

    /**
     * Submits a collection of {@code Runnable} to be run concurrently.
     *
     * @param runnables A collection of {@code Runnable} to be run concurrently.
     */
    public void runAll(final Collection<Runnable> runnables) {
        runnables.forEach(this::run);
    }

    @Override
    public void close() {
        runner.shutdownNow();
        runner.close();
    }
}
