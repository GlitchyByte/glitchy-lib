// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.workqueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * A queue of work with the following characteristics:
 * <ul>
 *     <li>Able to queue work items from multiple threads with minimal lock
 *     contention.
 *     <li>Work is serviced separately from the queue, allowing work to be
 *     performed without blocking the queue.
 * </ul>
 *
 * @param <T> Type of work item.
 */
public final class GAsyncWorkQueue<T> {

    private final List<T> queue = new LinkedList<>();
    private final Lock queueLock = new ReentrantLock();
    private final Condition itemQueued = queueLock.newCondition();
    private final List<T> workItems = new LinkedList<>();
    private final Lock workLock = new ReentrantLock();

    /**
     * Creates an asynchronous work queue.
     */
    public GAsyncWorkQueue() {
        // No-op.
    }

    /**
     * Adds a work item to the queue.
     *
     * @param item Work item.
     */
    public void addWork(final T item) {
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
     * <p>{@code awaitWork} and {@code doWork} must be called on the same thread.
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
                    workItems.addAll(queue);
                    queue.clear();
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
     * <p>{@code awaitWork} and {@code doWork} must be called on the same thread.
     *
     * @param itemConsumer Consumer that performs work on items.
     */
    public void doWork(final Consumer<T> itemConsumer) {
        workLock.lock();
        try {
            if (workItems.isEmpty()) {
                return;
            }
            workItems.forEach(itemConsumer);
            workItems.clear();
        } finally {
            workLock.unlock();
        }
    }
}
