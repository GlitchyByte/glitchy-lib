// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Event handler.
 *
 * <p>Designed to broadcast events in a single worker thread, but without
 * blocking the event receiver queue.
 * <p>Must be started with a {@code GTaskRunner}.
 */
public final class GEventHandler extends GConcurrentTask {

    private final Map<String, Collection<Consumer<GEvent>>> handlers = new HashMap<>();
    private final Lock handlersLock = new ReentrantLock();
    private final GAsyncWorkQueue<GEvent> events = new GAsyncWorkQueue<>();

    @Override
    public void run() {
        started();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                events.awaitWork();
                events.doWork(event -> {
                    GLock.locked(handlersLock, () -> {
                        final Collection<Consumer<GEvent>> consumers = handlers.get(event.key);
                        if (consumers != null) {
                            consumers.forEach(c -> c.accept(event));
                        }
                    });
                });
            }
        } catch (final InterruptedException e) {
            // We are out!
        }
    }

    /**
     * Registers a handler to the given key.
     *
     * @param key Event key.
     * @param handler Event handler.
     */
    public void registerEventHandler(final String key, final Consumer<GEvent> handler) {
        GLock.locked(handlersLock, () -> {
            final Collection<Consumer<GEvent>> consumers = handlers.computeIfAbsent(key, k -> new HashSet<>());
            consumers.add(handler);
        });
    }

    /**
     * Send event.
     *
     * @param event Event object.
     */
    public void send(final GEvent event) {
        events.queueWork(event);
    }

    /**
     * Send event.
     *
     * @param key Event key.
     * @param data Event data.
     */
    public void send(final String key, final Object data) {
        send(GEvent.createEvent(key, data));
    }

    /**
     * Send event with no data.
     *
     * @param key Event key.
     */
    public void send(final String key) {
        send(key, null);
    }
}
