// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.eventhandler;

import com.glitchybyte.glib.concurrent.GLock;
import com.glitchybyte.glib.concurrent.GTaskRunner;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Event receiver.
 *
 * <p>The event handler happens on its own thread. So there is a platform
 * thread created per receiver. The handler can take all the time it wants.
 * It will simply delay processing of their own events, but will never
 * block any senders or other receivers.
 */
public final class GEventReceiver implements AutoCloseable {

    private final GTaskRunner runner = new GTaskRunner(1);
    private final Consumer<GEvent> eventHandler;
    private final Queue<GEvent> events = new ConcurrentLinkedQueue<>();
    private boolean moreEvents = false;
    private final Lock eventsLock = new ReentrantLock();
    private final Condition eventReceived = eventsLock.newCondition();

    /**
     * Creates an event receiver with the given handler.
     *
     * @param eventHandler Event handler.
     */
    GEventReceiver(final Consumer<GEvent> eventHandler) {
        this.eventHandler = eventHandler;
        runner.run(this::processEvents);
    }

    @Override
    public void close() {
        runner.close();
    }

    /**
     * Adds an event to this receivers queue.
     *
     * <p>This is a non-blocking operation.
     *
     * @param event Event to queue.
     */
    public void addEvent(final GEvent event) {
        events.add(event);
        // We might have picked up the event already. So don't even bother if we are empty again.
        if (!events.isEmpty()) {
            // We simply indicate there might be more events and let go immediately.
            GLock.locked(eventsLock, () -> {
                moreEvents = true;
                eventReceived.signalAll();
            });
        }
    }

    private void processEvents() {
        // This is happening on its own thread.
        while (true) {
            final GEvent event = events.poll();
            if (event == null) {
                // If we can't find any other events, we await the signal for more events
                // which can be happening right now! before we even lock. So we check for
                // that within the lock.
                eventsLock.lock();
                try {
                    if (!moreEvents) {
                        eventReceived.await();
                        moreEvents = false;
                    }
                } catch (final InterruptedException e) {
                    // We out!
                    break;
                } finally {
                    eventsLock.unlock();
                }
            } else {
                // The handler can take all the time it wants.
                // It will simply delay processing of their own events.
                eventHandler.accept(event);
            }
        }
    }
}