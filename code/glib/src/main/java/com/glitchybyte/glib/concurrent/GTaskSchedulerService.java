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
public final class GTaskSchedulerService extends GTaskExecutorService<ScheduledExecutorService> implements GTaskScheduler {

    /**
     * Creates a task scheduler with a single worker thread.
     */
    public GTaskSchedulerService() {
        this(Executors.newSingleThreadScheduledExecutor(new GThreadFactory()));
    }

    /**
     * Creates a task scheduler with a fixed thread pool.
     *
     * @param threadCount Thread count for this scheduler.
     */
    public GTaskSchedulerService(final Integer threadCount) {
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
    public GTaskSchedulerService(final ScheduledExecutorService runner) {
        super(runner);
    }

    @Override
    public GCancelable schedule(final Duration delay, final Runnable runnable) {
        final var future = runner.schedule(
                runnable,
                delay.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }

    @Override
    public GCancelable scheduleAtFixedRate(final Duration initialDelay, final Duration period, final Runnable runnable) {
        final var future = runner.scheduleAtFixedRate(
                runnable,
                initialDelay.toMillis(),
                period.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }

    @Override
    public GCancelable scheduleWithFixedDelay(final Duration initialDelay, final Duration delay, final Runnable runnable) {
        final var future = runner.scheduleWithFixedDelay(
                runnable,
                initialDelay.toMillis(),
                delay.toMillis(),
                TimeUnit.MILLISECONDS
        );
        return () -> future.cancel(true);
    }
}
