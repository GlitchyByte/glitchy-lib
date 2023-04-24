// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread utilities.
 */
public final class GThreads {

    /**
     * Suspends thread execution until an interrupt happens.
     */
    public static void awaitInterrupt() {
        final Lock interruptLock = new ReentrantLock();
        final Condition interruptedCheck = interruptLock.newCondition();
        interruptLock.lock();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                interruptedCheck.await();
            }
        } catch (final InterruptedException e) {
            // Exit! This is what we want.
        } finally {
            interruptLock.unlock();
        }
    }

    private GThreads() {
        // Hiding constructor.
    }
}
