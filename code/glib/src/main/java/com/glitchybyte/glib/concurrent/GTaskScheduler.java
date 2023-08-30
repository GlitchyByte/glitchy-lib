// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import com.glitchybyte.glib.function.GCancelable;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A scheduler utility on top of {@link ScheduledExecutorService}.
 *
 * <p>This class standardizes the interface to use {@link Duration} for timings.
 */
public final class GTaskScheduler extends GTaskExecutor<ScheduledExecutorService> {

    /**
     * Creates a task scheduler with the given {@link ScheduledExecutorService}.
     *
     * <p>This scheduler owns the given {@link ScheduledExecutorService} and
     * will shut it down and close it when the scheduler is closed.
     *
     * @param runner {@link ScheduledExecutorService} to use as scheduler.
     */
    public GTaskScheduler(final ScheduledExecutorService runner) {
        super(runner);
    }

    /**
     * Creates a task scheduler with a default runner.
     */
    public GTaskScheduler() {
        this(Executors.newSingleThreadScheduledExecutor());
    }

    /**
     * Schedules a one-shot task.
     *
     * @param runnable Task to schedule.
     * @param delay Delay before task execution.
     * @return A {@link GCancelable} to cancel the task.
     */
    public GCancelable schedule(final Runnable runnable, final Duration delay) {
        final var future = runner.schedule(
                createRunnableWrapper(runnable),
                delay.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }

    /**
     * Schedules a task to run at a fixed delay.
     *
     * @param runnable Task to schedule.
     * @param initialDelay Delay before task execution.
     * @param period Cadence at which tasks run.
     * @return A {@link GCancelable} to cancel the task.
     */
    public GCancelable scheduleAtFixedRate(final Runnable runnable,
            final Duration initialDelay, final Duration period) {
        final var future = runner.scheduleAtFixedRate(
                createRunnableWrapper(runnable),
                initialDelay.toMillis(),
                period.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }

    /**
     * Schedules a task at a delay after each run.
     *
     * @param runnable Task to schedule.
     * @param initialDelay Delay before task execution.
     * @param delay Delay after task execution.
     * @return A {@link GCancelable} to cancel the task.
     */
    public GCancelable scheduleWithFixedDelay(final Runnable runnable,
            final Duration initialDelay, final Duration delay) {
        final var future = runner.scheduleWithFixedDelay(
                createRunnableWrapper(runnable),
                initialDelay.toMillis(),
                delay.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }
}
