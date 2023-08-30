// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * A task runner facility to run {@link GTask} tasks.
 *
 * <p>Classes ending in 'Task' in the concurrent package must be started with
 * a {@link GTaskRunner}.
 *
 * <p>This runner is also capable of running standalone {@link Runnable}s and
 * {@link Callable}s. This avoids having to create, or keep track of, a
 * separate {@link ExecutorService}.
 */
public final class GTaskRunner extends GTaskExecutor<ExecutorService> {

    /**
     * Creates a task runner with the given {@link ExecutorService}.
     *
     * <p>This runner owns the given {@link ExecutorService} and will shut it
     * down and close it when the runner is closed.
     *
     * @param runner {@link ExecutorService} to use as runner.
     */
    public GTaskRunner(final ExecutorService runner) {
        super(runner);
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

    /**
     * Submits a {@link Runnable} to execute concurrently.
     *
     * @param runnable A {@link Runnable} to execute.
     * @return A {@link Future<Void>} representing pending completion of the task.
     * @throws RejectedExecutionException If the task cannot be scheduled for execution.
     */
    @SuppressWarnings("unchecked")
    public Future<Void> run(final Runnable runnable) throws RejectedExecutionException {
        return (Future<Void>) runner.submit(createRunnableWrapper(runnable));
    }

    /**
     * Submits a collection of {@link Runnable} to execute concurrently.
     *
     * @param runnables A collection of {@link Runnable} to execute.
     * @return A list of {@link Future<Void>} representing pending completion of the tasks.
     * @throws RejectedExecutionException If one the tasks cannot be scheduled for execution.
     */
    public List<Future<Void>> runAll(final Collection<Runnable> runnables) throws RejectedExecutionException {
        return runnables.stream().map(this::run).toList();
    }

    /**
     * Submits an array of {@link Runnable} to execute concurrently.
     *
     * @param runnables An array of {@link Runnable} to execute.
     * @return A list of {@link Future<Void>} representing pending completion of the tasks.
     * @throws RejectedExecutionException If one the tasks cannot be scheduled for execution.
     */
    public List<Future<Void>> runAll(final Runnable[] runnables) throws RejectedExecutionException {
        return runAll(Arrays.asList(runnables));
    }

    /**
     * Submits a {@link Callable} to execute concurrently.
     *
     * @param callable A {@link Callable} to execute.
     * @return A {@link Future} representing pending completion of the task.
     * @param <V> Type of task result.
     * @throws RejectedExecutionException If the task cannot be scheduled for execution.
     */
    public <V> Future<V> call(final Callable<V> callable) throws RejectedExecutionException {
        return runner.submit(createCallableWrapper(callable));
    }

    /**
     * Submits a collection of {@link Callable} to execute concurrently.
     *
     * @param callables A collection of {@link Callable} to execute.
     * @return A list of {@link Future} representing pending completion of the tasks.
     * @param <V> Type of task result.
     * @throws RejectedExecutionException If one of the tasks cannot be scheduled for execution.
     */
    public <V> List<Future<V>> callAll(final Collection<Callable<V>> callables) throws RejectedExecutionException {
        return callables.stream().map(this::call).toList();
    }

    /**
     * Submits an array of {@link Callable} to execute concurrently.
     *
     * @param callables An array of {@link Callable} to execute.
     * @return A list of {@link Future} representing pending completion of the tasks.
     * @param <V> Type of task result.
     * @throws RejectedExecutionException If one the tasks cannot be scheduled for execution.
     */
    public <V> List<Future<V>> callAll(final Callable<V>[] callables) throws RejectedExecutionException {
        return callAll(Arrays.asList(callables));
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
     * @throws RejectedExecutionException If the task cannot be scheduled for execution.
     */
    public <T extends GTask> T start(final T task, final Duration timeout)
            throws InterruptedException, RejectedExecutionException {
        task.setTaskRunner(this);
        runner.execute(createTaskWrapper(task));
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
     * @throws RejectedExecutionException If the task cannot be scheduled for execution.
     */
    public <T extends GTask> T start(final T task) throws InterruptedException, RejectedExecutionException {
        return start(task, Duration.ofSeconds(5));
    }
}
