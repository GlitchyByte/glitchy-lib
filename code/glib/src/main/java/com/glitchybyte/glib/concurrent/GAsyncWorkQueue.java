// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import com.glitchybyte.glib.GArrays;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * A queue of work with the following characteristics:
 * <ul>
 *     <li>Queue work items from multiple threads with minimal lock contention.
 *     <li>Work is serviced separately from the queue. Allowing work to be
 *     performed without blocking the queue.
 * </ul>
 *
 * @param <T> Type of work item.
 */
public final class GAsyncWorkQueue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final Lock queueLock = new ReentrantLock();
    private final Condition itemQueued = queueLock.newCondition();
    private T[] workItems = null;
    private final Lock workLock = new ReentrantLock();

    /**
     * Creates an asynchronous work queue.
     */
    public GAsyncWorkQueue() {
        // No-op.
    }

    /**
     * Queues a work item.
     *
     * @param item Work item.
     */
    public void queueWork(final T item) {
        if (item == null) {
            throw new IllegalArgumentException("Work item cannot be null!");
        }
        queueLock.lock();
        try {
            queue.add(item);
            itemQueued.signalAll();
        } finally {
            queueLock.unlock();
        }
    }

    /**
     * Awaits for work to be queued.
     *
     * @throws InterruptedException If the thread is interrupted while awaiting.
     */
    public void awaitWork() throws InterruptedException {
        queueLock.lock();
        try {
            while (queue.isEmpty()) {
                itemQueued.await();
            }
            if (workLock.tryLock()) {
                try {
                    final int count = queue.size();
                    workItems = GArrays.createArray(count);
                    for (int i = 0; i < count; ++i) {
                        workItems[i] = queue.removeFirst();
                    }
                } finally {
                    workLock.unlock();
                }
            }
        } finally {
            queueLock.unlock();
        }
    }

    /**
     * Performs work on every item queued.
     *
     * @param itemConsumer Consumer that performs work on items.
     */
    public void doWork(final Consumer<T> itemConsumer) {
        workLock.lock();
        if (workItems == null) {
            return;
        }
        try {
            for (final T item: workItems) {
                itemConsumer.accept(item);
            }
            workItems = null;
        } finally {
            workLock.unlock();
        }
    }
}
