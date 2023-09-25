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
     * Creates a task scheduler with a single worker thread.
     */
    public GTaskScheduler() {
        this(Executors.newSingleThreadScheduledExecutor(new GThreadFactory()));
    }

    /**
     * Creates a task scheduler with a fixed thread pool.
     *
     * @param threadCount Thread count for this scheduler.
     */
    public GTaskScheduler(final Integer threadCount) {
        super(switch (threadCount) {
            case 1 -> Executors.newSingleThreadScheduledExecutor(new GThreadFactory());
            case Integer x when x > 1 -> Executors.newScheduledThreadPool(threadCount, new GThreadFactory());
            case null, default -> throw new IllegalArgumentException("threadCount must be positive!");
        });
    }

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
     * Schedules a one-shot task.
     *
     * @param delay Delay before task execution.
     * @param runnable Task to schedule.
     * @return A {@link GCancelable} to cancel the task.
     */
    public GCancelable schedule(final Duration delay, final Runnable runnable) {
        final var future = runner.schedule(
                runnable,
                delay.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }

    /**
     * Schedules a task to run at a fixed delay.
     *
     * @param initialDelay Delay before task execution.
     * @param period Cadence at which tasks run.
     * @param runnable Task to schedule.
     * @return A {@link GCancelable} to cancel the task.
     */
    public GCancelable scheduleAtFixedRate(final Duration initialDelay,
            final Duration period, final Runnable runnable) {
        final var future = runner.scheduleAtFixedRate(
                runnable,
                initialDelay.toMillis(),
                period.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }

    /**
     * Schedules a task at a delay after each run.
     *
     * @param initialDelay Delay before task execution.
     * @param delay Delay after task execution.
     * @param runnable Task to schedule.
     * @return A {@link GCancelable} to cancel the task.
     */
    public GCancelable scheduleWithFixedDelay(final Duration initialDelay,
            final Duration delay, final Runnable runnable) {
        final var future = runner.scheduleWithFixedDelay(
                runnable,
                initialDelay.toMillis(),
                delay.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }
}
