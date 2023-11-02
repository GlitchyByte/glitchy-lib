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
 * a {@link GTaskRunnerService}.
 *
 * <p>This runner is also capable of running standalone {@link Runnable}s and
 * {@link Callable}s. This avoids having to create, or keep track of, a separate
 * {@link ExecutorService}. Keep in mind this class uses platform threads.
 */
public final class GTaskRunnerService extends GTaskExecutorService<ExecutorService> implements GTaskRunner {

    /**
     * Creates a task runner with an unbounded cached thread pool.
     */
    public GTaskRunnerService() {
        super(Executors.newCachedThreadPool(new GThreadFactory()));
    }

    /**
     * Creates a task runner with a fixed thread pool.
     *
     * @param threadCount Thread count for this runner.
     */
    public GTaskRunnerService(final Integer threadCount) {
        super(switch (threadCount) {
            case 1 -> Executors.newSingleThreadExecutor(new GThreadFactory());
            case Integer x when x > 1 -> Executors.newFixedThreadPool(threadCount, new GThreadFactory());
            case null, default -> throw new IllegalArgumentException("threadCount must be positive!");
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Future<Void> run(final Runnable runnable) throws RejectedExecutionException {
        return (Future<Void>) runner.submit(runnable);
    }

    @Override
    public List<Future<Void>> runAll(final Collection<Runnable> runnables) throws RejectedExecutionException {
        return runnables.stream().map(this::run).toList();
    }

    @Override
    public List<Future<Void>> runAll(final Runnable[] runnables) throws RejectedExecutionException {
        return runAll(Arrays.asList(runnables));
    }

    @Override
    public <V> Future<V> call(final Callable<V> callable) throws RejectedExecutionException {
        return runner.submit(callable);
    }

    @Override
    public <V> List<Future<V>> callAll(final Collection<Callable<V>> callables) throws RejectedExecutionException {
        return callables.stream().map(this::call).toList();
    }

    @Override
    public <V> List<Future<V>> callAll(final Callable<V>[] callables) throws RejectedExecutionException {
        return callAll(Arrays.asList(callables));
    }

    @Override
    public <T extends GTask> T start(final T task, final Duration timeout)
            throws InterruptedException, RejectedExecutionException {
        task.setTaskRunner(this);
        runner.execute(createTaskWrapper(task));
        task.awaitStarted(timeout);
        return task;
    }

    @Override
    public <T extends GTask> T start(final T task) throws InterruptedException, RejectedExecutionException {
        return start(task, Duration.ofSeconds(5));
    }
}
