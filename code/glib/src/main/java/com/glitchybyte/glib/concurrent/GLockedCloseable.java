// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Defines an {@link AutoCloseable} resource that needs to keep track of its
 * closed status.
 *
 * <p>Helper methods prevent executing a method when the resource has been
 * closed already. And prevents actions taken on close from executing more
 * than once.
 *
 * <p>This is a convenience class for when a resource doesn't throw exceptions.
 */
public abstract class GLockedCloseable implements AutoCloseable {

    private boolean isClosed = false;
    private final ReadWriteLock isClosedLock = new ReentrantReadWriteLock();

    /**
     * Default empty constructor.
     */
    public GLockedCloseable() {
        // No-op.
    }

    /**
     * Closed status.
     *
     * @return True if closed.
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Actions to take to close the resource.
     *
     * @param runnable Actions to take to close the resource.
     */
    protected void lockClose(final Runnable runnable) {
        GLock.writeLocked(isClosedLock, () -> {
            if (!isClosed) {
                runnable.run();
                isClosed = true;
            }
        });
    }

    /**
     * Actions to take if resource is not closed.
     *
     * @param runnable Actions to take if resource is not closed.
     */
    protected void ifNotClosed(final Runnable runnable) {
        GLock.readLocked(isClosedLock, () -> {
            if (!isClosed) {
                runnable.run();
            }
        });
    }

    /**
     * Actions to take if resource is not closed, with a return value.
     *
     * @param defaultResult Default value to return if resourced is closed.
     * @param supplier Actions to take if resource is not closed, with a return value.
     * @return Value returned for this method.
     * @param <V> Type of value to return.
     */
    protected <V> V ifNotClosedResult(final V defaultResult, final Supplier<V> supplier) {
        return GLock.readLockedResult(isClosedLock, () -> isClosed ? defaultResult : supplier.get());
    }
}
