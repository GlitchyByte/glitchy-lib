// Copyright 2022-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.process;

import com.glitchybyte.glib.concurrent.GLock;
import com.glitchybyte.glib.concurrent.GTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Console output collector for capturing an external process output.
 *
 * <p>This class uses a buffer to accumulate lines, when lines are requested
 * the whole buffer is returned and a new buffer created for new lines.
 */
public final class GProcessOutputCollectorTask extends GTask {

    private final Process process;
    private final int maxOutputBufferLines;
    private LinkedList<String> outputLines;
    private final Lock outputLinesLock = new ReentrantLock();

    /**
     * Creates an output collector.
     *
     * @param process Process to which attach this collector.
     * @param maxOutputBufferLines Max lines to hold in memory. If output exceeds this quantity before being queried,
     *                             older lines will be lost. A value of zero will prevent any collection.
     */
    public GProcessOutputCollectorTask(final Process process, final int maxOutputBufferLines) {
        this.process = process;
        this.maxOutputBufferLines = maxOutputBufferLines;
        outputLines = maxOutputBufferLines == 0 ? null : createOutputBuffer();
    }

    private LinkedList<String> createOutputBuffer() {
        return new LinkedList<>();
    }

    /**
     * Collector runner. This must be called on a different thread.
     */
    @Override
    public void run() {
        try (final BufferedReader reader = process.inputReader(StandardCharsets.UTF_8)) {
            started();
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    addLineToOutput(line);
                }
            } while ((line != null) && !Thread.currentThread().isInterrupted());
        } catch (final IOException e) {
            // No-op.
        }
    }

    private void addLineToOutput(final String line) {
        if (maxOutputBufferLines == 0) {
            return;
        }
        GLock.locked(outputLinesLock, () -> {
            while (outputLines.size() >= maxOutputBufferLines) {
                outputLines.removeFirst();
            }
            outputLines.add(line);
        });
    }

    /**
     * Returns all collected output until this moment. Recreate collection buffer.
     *
     * @return All collected lines until this moment.
     */
    public List<String> getOutput() {
        if (maxOutputBufferLines == 0) {
            return Collections.emptyList();
        }
        return GLock.lockedResult(outputLinesLock, () -> {
            final List<String> output = outputLines;
            outputLines = createOutputBuffer();
            return output;
        });
    }
}
