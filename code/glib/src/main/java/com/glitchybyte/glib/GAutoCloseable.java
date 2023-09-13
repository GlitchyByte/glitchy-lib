// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import com.glitchybyte.glib.concurrent.GLock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Helper wrapper around {@link AutoCloseable} to facilitate throwing an
 * exception when a method is called and the resource is closed.
 *
 * <p>DESIGN NOTE: This helper does not take lambdas on purpose. This way if an
 * exception is thrown we don't pollute the stack trace.
 *
 * <p>In your {@code close} method:
 * {@snippet :
 *      try {
 *          startClose();
 *          // Your closing code here...
 *      } finally {
 *          endClose();
 *      }
 * }
 *
 * <p>In methods you want to protect:
 * {@snippet :
 *     try {
 *         startIfOpen();
 *        // Your method code here...
 *     } finally {
 *         endIfOpen();
 *     }
 * }
 */
public abstract class GAutoCloseable implements AutoCloseable {

    private boolean isClosed = false;
    private final ReadWriteLock closedLock = new ReentrantReadWriteLock();

    /**
     * Returns true if this resource is closed.
     *
     * @return True if this resource is closed.
     */
    public boolean isClosed() {
        return GLock.readLockedResult(closedLock, () -> isClosed);
    }

    /**
     * Start closing the resource.
     *
     * <p>This guarantees no other method is accessing the resource, and only
     * one invocation to {@code close} happens at a time.
     */
    protected void startClose() {
        closedLock.writeLock().lock();
        if (isClosed) {
            throw new IllegalStateException("Resource already closed!");
        }
    }

    /**
     * End closing the resource.
     */
    protected void endClose() {
        isClosed = true;
        closedLock.writeLock().unlock();
    }

    /**
     * Start a method if the resource is open.
     *
     * <p>This guarantees resource is not closed, and allows methods to
     * execute concurrently.
     */
    protected void startIfOpen() {
        closedLock.readLock().lock();
        if (isClosed) {
            throw new IllegalStateException("Resource is closed!");
        }
    }

    /**
     * End a method if the resource is open.
     */
    protected void endIfOpen() {
        closedLock.readLock().unlock();
    }
}
