// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import com.glitchybyte.glib.GStrings;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A task runner facility to run {@code GConcurrentTask} tasks.
 *
 * <p>Classes ending in 'Task' in the concurrent package must be started with
 * a {@link GTaskRunner}.
 *
 * <p>This runner is also capable of running standalone {@link Runnable}s and
 * {@link Callable}s. This avoids having to create, or keep track of, an
 * {@link ExecutorService}.
 */
public final class GTaskRunner implements AutoCloseable {

    private static final AtomicLong taskRunnerCount = new AtomicLong(1);

    private final ExecutorService runner;
    private final Lock runnerLock = new ReentrantLock();
    private final long runnerId = taskRunnerCount.getAndIncrement();
    private final AtomicLong taskCount = new AtomicLong(1);

    /**
     * Creates a task runner with the given {@link ExecutorService}.
     *
     * <p>This runner owns the given {@link ExecutorService} and will shut it
     * down and close it when the runner is closed.
     *
     * @param runner {@link ExecutorService} to use as runner.
     */
    public GTaskRunner(final ExecutorService runner) {
        this.runner = runner;
    }

// TODO: Enable when virtual threads are out of preview!
//
//    /**
//     * Creates a task runner.
//     *
//     * @param useVirtualThreads True to create a runner that uses virtual threads.
//     *                          False to create a runner that uses platform threads.
//     */
//    public GTaskRunner(final boolean useVirtualThreads) {
//        this(useVirtualThreads ?
//                Executors.newVirtualThreadPerTaskExecutor() :
//                Executors.newCachedThreadPool());
//    }
//
//    /**
//     * Creates a task runner with a default runner that uses virtual threads.
//     */
//    public GTaskRunner() {
//        this(true);
//    }

    /**
     * Creates a task runner with a default runner.
     */
    public GTaskRunner() {
        this(Executors.newCachedThreadPool());
    }

    private String createTaskThreadName() {
        return GStrings.format("Task-%s|%s",
                Long.toHexString(runnerId),
                Long.toHexString(taskCount.getAndIncrement())
        );
    }

    private Runnable createRunWrapper(final Runnable runnable) {
        return () -> {
            Thread.currentThread().setName(createTaskThreadName());
            runnable.run();
        };
    }

    /**
     * Submits a {@link Runnable} to execute concurrently.
     *
     * @param runnable A {@link Runnable} to execute.
     * @return A {@link Future<Void>} representing pending completion of the task.
     */
    @SuppressWarnings("unchecked")
    public Future<Void> run(final Runnable runnable) {
        return GLock.lockedResult(runnerLock, () -> (Future<Void>) runner.submit(createRunWrapper(runnable)));
    }

    /**
     * Submits a collection of {@link Runnable} to execute concurrently.
     *
     * @param runnables A collection of {@link Runnable} to execute.
     * @return A list of {@link Future<Void>} representing pending completion of the tasks.
     */
    public List<Future<Void>> runAll(final Collection<Runnable> runnables) {
        return runnables.stream().map(this::run).toList();
    }

    /**
     * Submits an array of {@link Runnable} to execute concurrently.
     *
     * @param runnables An array of {@link Runnable} to execute.
     * @return A list of {@link Future<Void>} representing pending completion of the tasks.
     */
    public List<Future<Void>> runAll(final Runnable[] runnables) {
        return runAll(Arrays.asList(runnables));
    }

    private <V> Callable<V> createCallWrapper(final Callable<V> callable) {
        return () -> {
            Thread.currentThread().setName(createTaskThreadName());
            return callable.call();
        };
    }

    /**
     * Submits a {@link Callable} to execute concurrently.
     *
     * @param callable A {@link Callable} to execute.
     * @return A {@link Future} representing pending completion of the task.
     * @param <V> Type of task result.
     */
    public <V> Future<V> call(final Callable<V> callable) {
        return GLock.lockedResult(runnerLock, () -> runner.submit(createCallWrapper(callable)));
    }

    /**
     * Submits a collection of {@link Callable} to execute concurrently.
     *
     * @param callables A collection of {@link Callable} to execute.
     * @return A list of {@link Future} representing pending completion of the tasks.
     * @param <V> Type of task result.
     */
    public <V> List<Future<V>> callAll(final Collection<Callable<V>> callables) {
        return callables.stream().map(this::call).toList();
    }

    /**
     * Submits an array of {@link Callable} to execute concurrently.
     *
     * @param callables An array of {@link Callable} to execute.
     * @return A list of {@link Future} representing pending completion of the tasks.
     * @param <V> Type of task result.
     */
    public <V> List<Future<V>> callAll(final Callable<V>[] callables) {
        return callAll(Arrays.asList(callables));
    }

    private String getTaskThreadName(final GTask task) {
        final String taskThreadName = task.getTaskThreadName();
        return taskThreadName == null ? createTaskThreadName() : taskThreadName;
    }

    private Runnable createTaskWrapper(final GTask task) {
        return () -> {
            Thread.currentThread().setName(getTaskThreadName(task));
            task.run();
            task.done();
        };
    }

    /**
     * Submits a task to execute concurrently.
     *
     * <p>If the task does not start within the given timeout, an
     * {@link IllegalStateException} will be raised as this is considered a
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
    public <T extends GTask> T start(final T task, final Duration timeout) throws InterruptedException {
        task.setTaskRunner(this);
        GLock.locked(runnerLock, () -> runner.execute(createTaskWrapper(task)));
        task.awaitStarted(timeout);
        return task;
    }

    /**
     * Submits a task to be run concurrently.
     *
     * <p>If the task does not start within 5 seconds, an
     * {@link IllegalStateException} will be raised as this is considered a
     * bug in the task implementation.
     *
     * <p>This method blocks until the task has started.
     *
     * @param task Task to start.
     * @return The started task.
     * @param <T> Task type.
     * @throws InterruptedException If the thread is interrupted while waiting for the task to start.
     */
    public <T extends GTask> T start(final T task) throws InterruptedException {
        return start(task, Duration.ofSeconds(5));
    }

    @Override
    public void close() {
        runner.shutdownNow();
        runner.close();
    }
}
