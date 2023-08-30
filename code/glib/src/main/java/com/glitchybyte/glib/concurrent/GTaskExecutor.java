// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import com.glitchybyte.glib.GStrings;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract class for task runner facilities.
 *
 * <p>Contains wrapper methods for {@link Runnable}s and {@link Callable}s
 * to standardize thread names.
 *
 * <p>IMPORTANT NOTE: Most of this class could be replaced with a ThreadFactory,
 * but with virtual threads coming up that code seems to be in flux, so we are
 * going to create a wrap around an already constructed thread object.
 *
 * @param <ES> An {@link ExecutorService} or descendant.
 */
public abstract class GTaskExecutor<ES extends ExecutorService> implements AutoCloseable {

    private static final AtomicLong taskRunnerCount = new AtomicLong(0);

    /**
     * Actual {@link ExecutorService} runner.
     */
    protected final ES runner;

    private final long runnerId = taskRunnerCount.getAndIncrement();
    private final AtomicLong taskCount = new AtomicLong(0);

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

    private String createTaskThreadName() {
        return GStrings.format("Task-%s|%s",
                Long.toHexString(runnerId),
                Long.toHexString(taskCount.getAndIncrement())
        );
    }

    /**
     * Creates a wrapper around the given {@link Runnable} that sets the thread name.
     *
     * @param runnable {@link Runnable} to wrap.
     * @return A wrapped {@link Runnable}.
     */
    protected Runnable createRunnableWrapper(final Runnable runnable) {
        return () -> {
            Thread.currentThread().setName(createTaskThreadName());
            runnable.run();
        };
    }

    /**
     * Creates a wrapper around the given {@link Callable} that sets the thread name.
     *
     * @param callable {@link Callable} to wrap.
     * @return A wrapped {@link Callable}.
     * @param <V> {@link Callable} return value type.
     */
    protected  <V> Callable<V> createCallableWrapper(final Callable<V> callable) {
        return () -> {
            Thread.currentThread().setName(createTaskThreadName());
            return callable.call();
        };
    }

    private String getTaskThreadName(final GTask task) {
        final String taskThreadName = task.getTaskThreadName();
        return taskThreadName == null ? createTaskThreadName() : taskThreadName;
    }

    /**
     * Creates a wrapper around the given {@link GTask} that sets the thread name.
     *
     * @param task {@link GTask} to wrap.
     * @return A wrapped {@link GTask} in a {@link Runnable}.
     */
    protected Runnable createTaskWrapper(final GTask task) {
        return () -> {
            Thread.currentThread().setName(getTaskThreadName(task));
            task.run();
            task.done();
        };
    }
}
