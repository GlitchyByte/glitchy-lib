// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.event;

import com.glitchybyte.glib.concurrent.GLock;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

public class GEventTest {

    @Test
    void canSendAndReceive() {
        final GEventLink link = new GEventLink();
        final String testEventKind = "com.glitchybyte.test.MyEvent";
        final GEvent event = new GEvent(testEventKind, 42);
        final AtomicBoolean gotEvent = new AtomicBoolean(false);
        final Lock lock = new ReentrantLock();
        final Condition eventProcessed = lock.newCondition();
        final GEventReceiver receiver = link.createEventReceiver(e -> {
            lock.lock();
            try {
                gotEvent.set(e.kind.equals(testEventKind) && (e.getDataAsInt() == 42));
                eventProcessed.signalAll();
            } finally {
                lock.unlock();
            }
        }).subscribeTo(testEventKind);
        link.send(event);
        final AtomicBoolean successfulTx = new AtomicBoolean(false);
        assertDoesNotThrow(
                () -> successfulTx.set(
                        GLock.awaitConditionWithTestAndTimeout(lock, eventProcessed, gotEvent::get, Duration.ofMillis(300))
                )
        );
        assertTrue(successfulTx.get());
        assertTrue(gotEvent.get());
        receiver.close();
    }

    @Test
    void doesNotReceiveOthers() {
        final GEventLink link = new GEventLink();
        final String testEventKind = "com.glitchybyte.test.MyEvent";
        final GEvent event = new GEvent(testEventKind, 42);
        final AtomicBoolean gotEvent = new AtomicBoolean(false);
        final Lock lock = new ReentrantLock();
        final Condition eventProcessed = lock.newCondition();
        final GEventReceiver receiver = link.createEventReceiver(e -> {
            lock.lock();
            try {
                gotEvent.set(e.kind.equals(testEventKind) && (e.getDataAsInt() == 42));
                eventProcessed.signalAll();
            } finally {
                lock.unlock();
            }
        }).subscribeTo("com.glitchybyte.test.DifferentEvent");
        link.send(event);
        final AtomicBoolean successfulTx = new AtomicBoolean(false);
        assertDoesNotThrow(
                () -> successfulTx.set(
                        GLock.awaitConditionWithTestAndTimeout(lock, eventProcessed, gotEvent::get, Duration.ofMillis(300))
                )
        );
        assertFalse(successfulTx.get());
        assertFalse(gotEvent.get());
        receiver.close();
    }

    @Test
    void subAndUnsub() {
        final GEventLink link = new GEventLink();
        final String testEventKind = "com.glitchybyte.test.MyEvent";
        final GEvent event = new GEvent(testEventKind, 42);
        final AtomicBoolean gotEvent = new AtomicBoolean(false);
        final Lock lock = new ReentrantLock();
        final Condition eventProcessed = lock.newCondition();
        // Sub.
        final GEventReceiver receiver = link.createEventReceiver(e -> {
            lock.lock();
            try {
                gotEvent.set(e.kind.equals(testEventKind) && (e.getDataAsInt() == 42));
                eventProcessed.signalAll();
            } finally {
                lock.unlock();
            }
        }).subscribeTo(testEventKind);
        link.send(event);
        final AtomicBoolean successfulTx = new AtomicBoolean(false);
        assertDoesNotThrow(
                () -> successfulTx.set(
                        GLock.awaitConditionWithTestAndTimeout(lock, eventProcessed, gotEvent::get, Duration.ofMillis(300))
                )
        );
        assertTrue(successfulTx.get());
        assertTrue(gotEvent.get());
        // Unsub.
        receiver.unsubscribeFrom(testEventKind);
        GLock.locked(lock, () -> gotEvent.set(false));
        link.send(event);
        successfulTx.set(false);
        assertDoesNotThrow(
                () -> successfulTx.set(
                        GLock.awaitConditionWithTestAndTimeout(lock, eventProcessed, gotEvent::get, Duration.ofMillis(300))
                )
        );
        assertFalse(successfulTx.get());
        assertFalse(gotEvent.get());
        receiver.close();
    }
}
