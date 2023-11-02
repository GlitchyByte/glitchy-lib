// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import com.glitchybyte.glib.function.GCancelable;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A scheduler utility on top of {@link ScheduledExecutorService}.
 *
 * <p>This class standardizes the interface to use {@link Duration} for timings.
 */
public interface GTaskScheduler extends Executor {

    /**
     * Schedules a one-shot task.
     *
     * @param delay Delay before task execution.
     * @param runnable Task to schedule.
     * @return A {@link GCancelable} to cancel the task.
     */
    GCancelable schedule(final Duration delay, final Runnable runnable);

    /**
     * Schedules a task to run at a fixed delay.
     *
     * @param initialDelay Delay before task execution.
     * @param period Cadence at which tasks run.
     * @param runnable Task to schedule.
     * @return A {@link GCancelable} to cancel the task.
     */
    GCancelable scheduleAtFixedRate(final Duration initialDelay, final Duration period, final Runnable runnable);

    /**
     * Schedules a task at a delay after each run.
     *
     * @param initialDelay Delay before task execution.
     * @param delay Delay after task execution.
     * @param runnable Task to schedule.
     * @return A {@link GCancelable} to cancel the task.
     */
    GCancelable scheduleWithFixedDelay(final Duration initialDelay, final Duration delay, final Runnable runnable);
}
