// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import com.glitchybyte.glib.GStrings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Line collector.
 *
 * <p>This class reads lines from a {@code BufferedReader} and collects them
 * in an internal buffer. The buffer will not hold more than a set number of
 * lines. So it will never eat up more memory than it should. And it will
 * report if any lines are missing if it happens.
 *
 * <p>It is up to the owner of the collector to read the contents of the
 * buffer and do something useful with it.
 */
public final class GLineCollector implements AutoCloseable {

    /**
     * Default max lines used in the default constructor.
     */
    public static final int DEFAULT_MAX_LINES = 100;

    private final ExecutorService runner = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isActive = new AtomicBoolean(false);
    private final BufferedReader reader;
    private final int maxLines;
    private final Deque<String> lines;
    private int skipped = 0;

    /**
     * Creates a line collector from the given {@code BufferedReader} with
     * a specified max buffer.
     *
     * @param reader Line producer.
     * @param maxLines Max number of lines to retain in buffer.
     */
    public GLineCollector(final BufferedReader reader, final int maxLines) {
        this.reader = reader;
        this.maxLines = maxLines;
        lines = new ArrayDeque<>(maxLines + 1);
    }

    /**
     * Creates a line collector from the given {@code BufferedReader} with
     * a default max buffer.
     *
     * @param reader Line producer.
     */
    public GLineCollector(final BufferedReader reader) {
        this(reader, DEFAULT_MAX_LINES);
    }

    /**
     * Start collecting lines.
     *
     * @return This collector.
     */
    public synchronized GLineCollector start() {
        if (isActive.get()) {
            throw new IllegalStateException("Line collector already started!");
        }
        runner.execute(this::collect);
        isActive.set(true);
        return this;
    }

    private void collect() {
        Thread.currentThread().setName("log-collector");
        try {
            reader.lines().forEachOrdered(this::putLine);
        } catch (final UncheckedIOException e) {
            // We stop collecting.
        }
        isActive.set(false);
    }

    /**
     * Returns true if this collector is active and collecting lines.
     *
     * @return True if this collector is active and collecting lines.
     */
    public boolean isActive() {
        return isActive.get();
    }

    /**
     * Waits up to the given timeout for a line to be available.
     *
     * @param timeout Maximum time to wait, in milliseconds.
     * @throws InterruptedException If the wait is interrupted.
     */
    public synchronized void await(final long timeout) throws InterruptedException {
        if (!lines.isEmpty()) {
            // Don't wait if we have lines.
            return;
        }
        wait(timeout);
    }

    /**
     * Waits indefinitely for a line to be available.
     *
     * @throws InterruptedException If the wait is interrupted.
     */
    public synchronized void await() throws InterruptedException {
        await(0);
    }

    private synchronized void putLine(final String line) {
        if (lines.size() >= maxLines) {
            ++skipped;
            lines.pollFirst();
        }
        lines.offerLast(line);
        notify();
    }

    private String getSkippedLines() {
        return GStrings.format("(Skipped log lines: %d)", skipped);
    }

    /**
     * Returns the next line in the buffer or null of there is none.
     *
     * <p>This removes that line from the buffer.
     *
     * @return The next line in the buffer or null of there is none.
     */
    public synchronized String takeLine() {
        if (skipped > 0) {
            final String line = getSkippedLines();
            skipped = 0;
            return line;
        }
        return lines.pollFirst();
    }

    /**
     * Returns all available lines in the buffer.
     *
     * <p>This removes all lines from the buffer.
     *
     * @return All available lines in the buffer.
     */
    public synchronized List<String> takeAvailableLines() {
        if (lines.isEmpty()) {
            return Collections.emptyList();
        }
        if (skipped > 0) {
            lines.offerFirst(getSkippedLines());
            skipped = 0;
        }
        final List<String> result = lines.stream().toList();
        lines.clear();
        return result;
    }

    @Override
    public synchronized void close() {
        runner.shutdownNow();
        runner.close();
        try {
            reader.close();
        } catch (final IOException e) {
            // Nothing we can do now. We out!
        }
    }
}
