// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Event link.
 *
 * <p>This is the link between sender and receiver. This link implements
 * and acts as a sender itself. Consider capturing {@code GEventSender}
 * interface only, unless you actually need to create a new receiver.
 *
 * <p>Designed to queue events without blocking.
 */
public final class GEventLink implements GEventSender {

    private final Map<String, Set<GEventReceiver>> eventTypeRegistry = new HashMap<>();
    private final ReadWriteLock eventTypeRegistryLock = new ReentrantReadWriteLock();

    /**
     * Creates an event link.
     */
    public GEventLink() {
        // No-op.
    }

    /**
     * Creates an event receiver linked to this event link.
     *
     * @param eventHandler Event handler.
     * @return A new event receiver.
     */
    public GEventReceiver createEventReceiver(final Consumer<GEvent> eventHandler) {
        return new GEventReceiver(this, eventHandler);
    }

    /**
     * Registers a receiver to the given event type.
     *
     * @param receiver Event receiver.
     * @param eventType Event type for which to register receiver.
     */
    void registerEventReceiver(final GEventReceiver receiver, final String eventType) {
        eventTypeRegistryLock.writeLock().lock();
        try {
            final Set<GEventReceiver> receivers = eventTypeRegistry.computeIfAbsent(eventType, t -> new HashSet<>());
            receivers.add(receiver);
        } finally {
            eventTypeRegistryLock.writeLock().unlock();
        }
    }

    /**
     * De-registers a receiver from the given event type.
     *
     * @param receiver Event receiver.
     * @param eventType Event type for which to de-register receiver.
     */
    void deregisterEventReceiver(final GEventReceiver receiver, final String eventType) {
        eventTypeRegistryLock.writeLock().lock();
        try {
            final Set<GEventReceiver> receivers = eventTypeRegistry.get(eventType);
            if (receivers == null) {
                return;
            }
            receivers.remove(receiver);
            if (receivers.isEmpty()) {
                eventTypeRegistry.remove(eventType);
            }
        } finally {
            eventTypeRegistryLock.writeLock().unlock();
        }
    }

    /**
     * De-registers a receiver from all event types.
     *
     * @param receiver Event receiver.
     */
    void deregisterEventReceiver(final GEventReceiver receiver) {
        eventTypeRegistryLock.writeLock().lock();
        try {
            for (final var iterator = eventTypeRegistry.entrySet().iterator(); iterator.hasNext();) {
                final var entry = iterator.next();
                final Set<GEventReceiver> receivers = entry.getValue();
                receivers.remove(receiver);
                if (receivers.isEmpty()) {
                    iterator.remove();
                }
            }
        } finally {
            eventTypeRegistryLock.writeLock().unlock();
        }
    }

    @Override
    public void send(final GEvent event) {
        eventTypeRegistryLock.readLock().lock();
        try {
            final Set<GEventReceiver> receivers = eventTypeRegistry.get(event.type);
            if (receivers == null) {
                return;
            }
            for (final GEventReceiver receiver: receivers) {
                receiver.postEvent(event);
            }
        } finally {
            eventTypeRegistryLock.readLock().unlock();
        }
    }

    @Override
    public void send(final String eventType, final Object data) {
        send(new GEvent(eventType, data));
    }

    @Override
    public void send(final String eventType) {
        send(eventType, null);
    }
}
