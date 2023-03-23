// Copyright 2020-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import com.glitchybyte.glib.concurrent.GLock;
import sun.misc.Signal;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility to monitor SIGINT and SIGTERM for proper application shutdown.
 */
public final class GShutdownMonitor {

    private static final AtomicBoolean generalShutdownInitiated = new AtomicBoolean(false);
    private static final Lock generalShutdownLock = new ReentrantLock();
    private static final Collection<GShutdownMonitor> shutdownMonitors = new HashSet<>();

    /**
     * Creates a monitor that will get notified when it's time for an orderly shutdown.
     *
     * @return A shutdown monitor.
     */
    public static GShutdownMonitor createShutdownMonitor() {
        generalShutdownLock.lock();
        try {
            final boolean isShuttingDown = generalShutdownInitiated.get();
            final GShutdownMonitor monitor = new GShutdownMonitor(isShuttingDown);
            if (!isShuttingDown) {
                shutdownMonitors.add(monitor);
            }
            return monitor;
        } finally {
            generalShutdownLock.unlock();
        }
    }

    private static void triggerShutdown(final Signal signal) {
        if (!generalShutdownInitiated.compareAndSet(false, true)) {
            return;
        }
        GLock.locked(generalShutdownLock, () -> {
            shutdownMonitors.forEach(GShutdownMonitor::shutdown);
            shutdownMonitors.clear();
        });
    }

    static {
        Signal.handle(new Signal("TERM"), GShutdownMonitor::triggerShutdown);
        Signal.handle(new Signal("INT"), GShutdownMonitor::triggerShutdown);
    }

    private volatile boolean isShuttingDown;
    private final Lock shutdownLock = new ReentrantLock();
    private final Condition shuttingDown  = shutdownLock.newCondition();

    private GShutdownMonitor(final boolean isShuttingDown) {
        this.isShuttingDown = isShuttingDown;
    }

    /**
     * Returns true when an orderly shutdown should occur.
     *
     * @return True when an orderly shutdown should occur.
     */
    public boolean shouldShutdown() {
        return isShuttingDown;
    }

    /**
     * Manually triggers an orderly shutdown.
     */
    public void shutdown() {
        isShuttingDown = true;
        GLock.signalAll(shutdownLock, shuttingDown);
    }

    /**
     * Awaits for a shutdown or expiration of the given timeout.
     *
     * <p>If a shutdown has been triggered, the method will exit fast.
     *
     * @param timeout Time to wait for shutdown.
     */
    public void awaitShutdown(final Duration timeout) {
        if (shouldShutdown()) {
            return;
        }
        try {
            GLock.awaitConditionWithTimeout(shutdownLock, shuttingDown, timeout);
        } catch (final InterruptedException e) {
            // This await is used to exit the app, so there is no need to broadcast
            // an InterruptedException. We are going to exit anyway.
            // So we eat it and save the caller from the extra boilerplate.
        }
    }

    /**
     * Awaits for a shutdown.
     *
     * <p>If a shutdown has been triggered, the method will exit fast.
     */
    public void awaitShutdown() {
        if (shouldShutdown()) {
            return;
        }
        try {
            GLock.awaitConditionWithTest(shutdownLock, shuttingDown, this::shouldShutdown);
        } catch (final InterruptedException e) {
            // This await is used to exit the app, so there is no need to broadcast
            // an InterruptedException. We are going to exit anyway.
            // So we eat it and save the caller from the extra boilerplate.
        }
    }

    /**
     * Convenience method to execute an action periodically at the given cadence,
     * until a shutdown is triggered.
     *
     * @param cadence Cadence at which to execute the action.
     * @param action Action to execute.
     */
    public void whileLive(final Duration cadence, final Runnable action) {
        while (!shouldShutdown()) {
            action.run();
            awaitShutdown(cadence);
        }
    }
}
