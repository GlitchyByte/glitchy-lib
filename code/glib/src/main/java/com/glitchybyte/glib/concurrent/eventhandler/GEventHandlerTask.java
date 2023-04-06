// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.eventhandler;

import com.glitchybyte.glib.concurrent.GTask;
import com.glitchybyte.glib.concurrent.GLock;
import com.glitchybyte.glib.concurrent.workqueue.GWorkQueue;
import com.glitchybyte.glib.concurrent.workqueue.GWorkQueueTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Event handler task.
 *
 * <p>Designed to broadcast events in a single worker thread, but without
 * blocking the event receiver queue.
 */
public final class GEventHandlerTask extends GTask implements GEventHandler {

    private final Map<String, Collection<Consumer<GEvent>>> handlers = new HashMap<>();
    private final ReadWriteLock handlersLock = new ReentrantReadWriteLock();
    private final GWorkQueueTask<GEvent> events;

    /**
     * Creates an event handler.
     */
    public GEventHandlerTask() {
        events = new GWorkQueue.Builder<GEvent>()
                .withProcessor(this::processEvent)
                .build();
    }

    private void processEvent(final GEvent event) {
        final Collection<Runnable> blocks;
        handlersLock.readLock().lock();
        try {
            final Collection<Consumer<GEvent>> consumers = handlers.get(event.key);
            if (consumers == null) {
                return;
            }
            blocks = consumers.stream()
                    .<Runnable>map(consumer -> () -> consumer.accept(event))
                    .toList();
        } finally {
            handlersLock.readLock().unlock();
        }
        getTaskRunner().runAll(blocks);
    }

    @Override
    public void run() {
        try {
            getTaskRunner().start(events);
            started();
        } catch (final InterruptedException e) {
            // We are out!
        }
    }

    @Override
    public void registerHandler(final String key, final Consumer<GEvent> handler) {
        GLock.writeLocked(handlersLock, () -> {
            final Collection<Consumer<GEvent>> consumers = handlers.computeIfAbsent(key, k -> new HashSet<>());
            consumers.add(handler);
        });
    }

    @Override
    public void send(final GEvent event) {
        events.addWork(event);
    }

    @Override
    public void send(final String key, final Object data) {
        send(new GEvent(key, data));
    }

    @Override
    public void send(final String key) {
        send(key, null);
    }
}
