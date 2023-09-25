// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.eventhandler;

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

    private final Map<String, Set<GEventReceiver>> kindRegistry = new HashMap<>();
    private final ReadWriteLock kindRegistryLock = new ReentrantReadWriteLock();

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
     * Registers a receiver to the given kind.
     *
     * @param receiver Event receiver.
     * @param kind Kind of event for which to register receiver.
     */
    void registerEventReceiver(final GEventReceiver receiver, final String kind) {
        kindRegistryLock.writeLock().lock();
        try {
            final Set<GEventReceiver> receivers = kindRegistry.computeIfAbsent(kind, k -> new HashSet<>());
            receivers.add(receiver);
        } finally {
            kindRegistryLock.writeLock().unlock();
        }
    }

    /**
     * Registers a receiver to a group of kinds.
     *
     * @param receiver Event receiver.
     * @param kinds Kinds of events for which to register receiver.
     */
    void registerEventReceiver(final GEventReceiver receiver, final Set<String> kinds) {
        kinds.forEach(kind -> registerEventReceiver(receiver, kind));
    }

    /**
     * De-registers a receiver from the given kind.
     *
     * @param receiver Event receiver.
     * @param kind Kind of event for which to de-register receiver.
     */
    void deregisterEventReceiver(final GEventReceiver receiver, final String kind) {
        kindRegistryLock.writeLock().lock();
        try {
            final Set<GEventReceiver> kindReceivers = kindRegistry.get(kind);
            if (kindReceivers == null) {
                return;
            }
            kindReceivers.remove(receiver);
            if (kindReceivers.isEmpty()) {
                kindRegistry.remove(kind);
            }
        } finally {
            kindRegistryLock.writeLock().unlock();
        }
    }

    /**
     * De-registers a receiver from a group of kinds.
     *
     * @param receiver Event receiver.
     * @param kinds Kinds of events for which to de-register receiver.
     */
    void deregisterEventReceiver(final GEventReceiver receiver, final Set<String> kinds) {
        kinds.forEach(kind -> deregisterEventReceiver(receiver, kind));
    }

    /**
     * De-registers a receiver from all kinds.
     *
     * @param receiver Event receiver.
     */
    void deregisterEventReceiver(final GEventReceiver receiver) {
        kindRegistryLock.writeLock().lock();
        try {
            for (final var iterator = kindRegistry.entrySet().iterator(); iterator.hasNext();) {
                final var entry = iterator.next();
                final Set<GEventReceiver> kindReceivers = entry.getValue();
                kindReceivers.remove(receiver);
                if (kindReceivers.isEmpty()) {
                    iterator.remove();
                }
            }
        } finally {
            kindRegistryLock.writeLock().unlock();
        }
    }

    @Override
    public void send(final GEvent event) {
        kindRegistryLock.readLock().lock();
        try {
            final Set<GEventReceiver> receivers = kindRegistry.get(event.kind);
            if (receivers == null) {
                return;
            }
            for (final GEventReceiver receiver: receivers) {
                receiver.postEvent(event);
            }
        } finally {
            kindRegistryLock.readLock().unlock();
        }
    }

    @Override
    public void send(final String kind, final Object data) {
        send(new GEvent(kind, data));
    }

    @Override
    public void send(final String kind) {
        send(kind, null);
    }
}
