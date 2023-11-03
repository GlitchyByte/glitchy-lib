// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

public class GTaskRunnerMultiTest {

    private GTaskRunnerService runner;

    @BeforeEach
    void setupRunner() {
        runner = new GTaskRunnerService();
    }

    @AfterEach
    void teardownRunner() {
        runner.close();
        runner = null;
    }

    @Test
    void canRun() {
        final List<String> items = new LinkedList<>();
        final Lock itemsLock = new ReentrantLock();
        final Runnable task = () -> GLock.locked(itemsLock, () -> items.add("one"));
        final CompletableFuture<Void> future = runner.run(task);
        final AtomicBoolean flip = new AtomicBoolean(false);
        future.thenRun(() -> flip.set(true)).join();
        try {
            future.get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        assertTrue(items.contains("one"));
        assertTrue(flip::get);
    }

    @Test
    void canRunCollection() {
        final List<String> items = new LinkedList<>();
        final Lock itemsLock = new ReentrantLock();
        final List<Runnable> tasks = List.of(
                () -> GLock.locked(itemsLock, () -> items.add("one")),
                () -> GLock.locked(itemsLock, () -> items.add("two")),
                () -> GLock.locked(itemsLock, () -> items.add("three"))
        );
        final List<CompletableFuture<Void>> futures = runner.runAll(tasks);
        futures.forEach(future -> {
            try {
                future.get();
            } catch (final InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(items.contains("one"));
        assertTrue(items.contains("two"));
        assertTrue(items.contains("three"));
    }

    @Test
    void canRunArray() {
        final List<String> items = new LinkedList<>();
        final Lock itemsLock = new ReentrantLock();
        final Runnable[] tasks = new Runnable[] {
                () -> GLock.locked(itemsLock, () -> items.add("one")),
                () -> GLock.locked(itemsLock, () -> items.add("two")),
                () -> GLock.locked(itemsLock, () -> items.add("three"))
        };
        final List<CompletableFuture<Void>> futures = runner.runAll(tasks);
        futures.forEach(future -> {
            try {
                future.get();
            } catch (final InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(items.contains("one"));
        assertTrue(items.contains("two"));
        assertTrue(items.contains("three"));
    }

    @Test
    void canCall() {
        final Callable<String> task = () -> "one";
        final CompletableFuture<String> future = runner.call(task);
        final AtomicBoolean flip = new AtomicBoolean(false);
        future.thenRun(() -> flip.set(true)).join();
        final String item;
        try {
            item = future.get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        assertEquals("one", item);
        assertTrue(flip::get);
    }

    @Test
    void canCallCollection() {
        final List<Callable<String>> tasks = List.of(
                () -> "one",
                () -> "two",
                () -> "three"
        );
        final List<CompletableFuture<String>> futures = runner.callAll(tasks);
        final List<String> items = new ArrayList<>();
        final Lock itemsLock = new ReentrantLock();
        futures.forEach(future -> {
            final String item;
            try {
                item = future.get();
            } catch (final InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            GLock.locked(itemsLock, () -> items.add(item));
        });
        assertTrue(items.contains("one"));
        assertTrue(items.contains("two"));
        assertTrue(items.contains("three"));
    }

    @Test
    void canCallArray() {
        @SuppressWarnings("unchecked") final Callable<String>[] tasks = new Callable[] {
                () -> "one",
                () -> "two",
                () -> "three"
        };
        final List<CompletableFuture<String>> futures = runner.callAll(tasks);
        final List<String> items = new ArrayList<>();
        final Lock itemsLock = new ReentrantLock();
        futures.forEach(future -> {
            final String item;
            try {
                item = future.get();
            } catch (final InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            GLock.locked(itemsLock, () -> items.add(item));
        });
        assertTrue(items.contains("one"));
        assertTrue(items.contains("two"));
        assertTrue(items.contains("three"));
    }

    private static final class SimpleTask extends GTask {

        private final List<String> items;

        public SimpleTask(final List<String> items) {
            this.items = items;
        }

        @Override
        public void run() {
            items.add("one");
            started();
        }
    }

    @Test
    void canStartTask() {
        final List<String> items = Collections.synchronizedList(new ArrayList<>());
        final GTask task = new SimpleTask(items);
        assertDoesNotThrow(() -> runner.start(task));
        assertTrue(items.contains("one"));
    }

    private static final class SlowStartTask extends GTask {

        private final List<String> items;

        public SlowStartTask(final List<String> items) {
            this.items = items;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1_000);
            } catch (final InterruptedException e) {
                // No-op.
            }
            started();
            items.add("one");
        }
    }

    @Test
    void canTimeoutTask() {
        final List<String> items = Collections.synchronizedList(new ArrayList<>());
        final GTask task = new SlowStartTask(items);
        assertThrowsExactly(IllegalStateException.class, () -> runner.start(task, Duration.ofMillis(300)));
        assertFalse(items.contains("one"));
    }

    private static final class SlowTask extends GTask {

        private final List<String> items;

        public SlowTask(final List<String> items) {
            this.items = items;
        }

        @Override
        public void run() {
            started();
            try {
                Thread.sleep(1_000);
            } catch (final InterruptedException e) {
                items.add("interrupted");
                return;
            }
            items.add("one");
        }
    }

    @Test
    void canInterruptTask() {
        final List<String> items = Collections.synchronizedList(new ArrayList<>());
        final GTask task = new SlowTask(items);
        assertDoesNotThrow(() -> runner.start(task));
        task.interrupt();
        assertDoesNotThrow(task::awaitDone);
        assertTrue(items.contains("interrupted"));
        assertFalse(items.contains("one"));
    }
}
