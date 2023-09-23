// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.eventhandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Event link.
 *
 * <p>This is the link between sender and receiver. This is where a client
 * registers their receiver to a particular kind of event. This link implements
 * and acts as a sender itself. Consider capturing {@code GEventSender}
 * interface only when you don't need to register a new receiver.
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
     * Registers a receiver with this link to the given kind.
     *
     * @param receiver Event receiver.
     * @param kind Kind of event for which to register receiver.
     */
    public void registerEventReceiver(final GEventReceiver receiver, final String kind) {
        kindRegistryLock.writeLock().lock();
        try {
            final Set<GEventReceiver> receivers = kindRegistry.computeIfAbsent(kind, k -> new HashSet<>());
            receivers.add(receiver);
        } finally {
            kindRegistryLock.writeLock().unlock();
        }
    }

    /**
     * Registers a receiver with this link to a group of kinds.
     *
     * @param receiver Event receiver.
     * @param kinds Kinds of events for which to register receiver.
     */
    public void registerEventReceiver(final GEventReceiver receiver, final Set<String> kinds) {
        kinds.forEach(kind -> registerEventReceiver(receiver, kind));
    }

    /**
     * De-registers a receiver with this link.
     *
     * @param receiver Event receiver.
     * @param kind Kind of event for which to de-register receiver.
     */
    public void deregisterEventReceiver(final GEventReceiver receiver, final String kind) {
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
     * De-registers a receiver with this link to a group of kinds.
     *
     * @param receiver Event receiver.
     * @param kinds Kinds of events for which to de-register receiver.
     */
    public void deregisterEventReceiver(final GEventReceiver receiver, final Set<String> kinds) {
        kinds.forEach(kind -> deregisterEventReceiver(receiver, kind));
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
                receiver.addEvent(event);
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
