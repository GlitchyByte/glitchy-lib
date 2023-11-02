// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.event;

import com.glitchybyte.glib.concurrent.GLock;
import com.glitchybyte.glib.concurrent.GTaskRunnerService;

import java.util.Queue;
import java.util.Set;
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

    private final GTaskRunnerService runner = new GTaskRunnerService(1);
    private final GEventLink link;
    private final Consumer<GEvent> eventHandler;
    private boolean isClosed = false;
    private final Queue<GEvent> events = new ConcurrentLinkedQueue<>();
    private boolean moreEvents = false;
    private final Lock eventsLock = new ReentrantLock();
    private final Condition eventReceived = eventsLock.newCondition();

    /**
     * Creates an event receiver with the given handler.
     *
     * @param eventHandler Event handler.
     */
    GEventReceiver(final GEventLink link, final Consumer<GEvent> eventHandler) {
        this.link = link;
        this.eventHandler = eventHandler;
        runner.run(this::processEvents);
    }

    @Override
    public void close() {
        link.deregisterEventReceiver(this);
        GLock.locked(eventsLock, () -> {
            isClosed = true;
            eventReceived.signalAll();
        });
        runner.close();
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
                    if (isClosed) {
                        break;
                    } else if (!moreEvents) {
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

    /**
     * Subscribes this receiver to the given kind of event.
     *
     * @param kind Kind of event.
     * @return This receiver.
     */
    public GEventReceiver subscribeTo(final String kind) {
        link.registerEventReceiver(this, kind);
        return this;
    }

    /**
     * Subscribes this receiver to the given group of kinds of events.
     *
     * @param kinds A group of kinds of events.
     * @return This receiver.
     */
    public GEventReceiver subscribeTo(final Set<String> kinds) {
        kinds.forEach(this::subscribeTo);
        return this;
    }

    /**
     * Unsubscribes this receiver from the given kind of event.
     *
     * @param kind Kind of event.
     * @return This receiver.
     */
    public GEventReceiver unsubscribeFrom(final String kind) {
        link.deregisterEventReceiver(this, kind);
        return this;
    }

    /**
     * Unsubscribes this receiver from the given group of kinds of events.
     *
     * @param kinds A group of kinds of events.
     * @return This receiver.
     */
    public GEventReceiver unsubscribeFrom(final Set<String> kinds) {
        kinds.forEach(this::unsubscribeFrom);
        return this;
    }

    /**
     * Unsubscribes this receiver from all kinds of events.
     *
     * @return This receiver.
     */
    public GEventReceiver unsubscribeFromAll() {
        link.deregisterEventReceiver(this);
        return this;
    }

    /**
     * Adds an event to this receivers queue.
     *
     * <p>This is a non-blocking operation.
     *
     * @param event Event to queue.
     */
    public void postEvent(final GEvent event) {
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
}
