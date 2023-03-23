// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.BooleanSupplier;

/**
 * Lock utilities.
 *
 * <p>This utility class contains common locking constructs.
 * They are not to replace all use cases, just one-lining common quick
 * operations.
 */
public final class GLock {

    /**
     * Convenience method to run a block of code between lock and unlock.
     *
     * @param lock Lock.
     * @param runnable Block of code.
     */
    public static void locked(final Lock lock, final Runnable runnable) {
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Acquire lock and awaits on the condition.
     *
     * @param lock Lock.
     * @param condition Condition.
     * @throws InterruptedException If interrupted while awaiting.
     */
    public static void awaitCondition(final Lock lock, final Condition condition) throws InterruptedException {
        lock.lock();
        try {
            condition.await();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Acquire lock and awaits on the condition up to the given timeout.
     *
     * @param lock Lock.
     * @param condition Condition.
     * @param timeout Await timeout.
     * @return True if condition was signaled. False if await timed out.
     * @throws InterruptedException If interrupted while awaiting.
     */
    public static boolean awaitConditionWithTimeout(final Lock lock, final Condition condition, final Duration timeout)
            throws InterruptedException {
        lock.lock();
        try {
            return condition.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Acquire lock and awaits on the condition until test is true.
     *
     * @param lock Lock.
     * @param condition Condition.
     * @param test Test to await for.
     * @throws InterruptedException If interrupted while awaiting.
     */
    public static void awaitConditionWithTest(final Lock lock, final Condition condition, final BooleanSupplier test)
            throws InterruptedException {
        lock.lock();
        try {
            while (!test.getAsBoolean()) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Signal all monitors on the given condition.
     *
     * @param lock Lock.
     * @param condition Condition.
     */
    public static void signalAll(final Lock lock, final Condition condition) {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Convenience method to run a block of code between a read-lock and unlock.
     *
     * @param lock Read/write lock.
     * @param runnable Block of code.
     */
    public static void readLocked(final ReadWriteLock lock, final Runnable runnable) {
        locked(lock.readLock(), runnable);
    }

    /**
     * Convenience method to run a block of code between a write-lock and unlock.
     *
     * @param lock Read/write lock.
     * @param runnable Block of code.
     */
    public static void writeLocked(final ReadWriteLock lock, final Runnable runnable) {
        locked(lock.writeLock(), runnable);
    }

    private GLock() {
        // Hiding constructor.
    }
}
