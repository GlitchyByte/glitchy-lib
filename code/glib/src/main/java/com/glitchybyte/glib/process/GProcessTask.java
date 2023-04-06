// Copyright 2022-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.process;

import com.glitchybyte.glib.concurrent.GLock;
import com.glitchybyte.glib.concurrent.GTask;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An encapsulation that represents and manages a system process.
 */
public final class GProcessTask extends GTask {

    /**
     * Process state.
     */
    public enum State {
        /**
         * Process created.
         */
        CREATED,

        /**
         * Process started.
         */
        STARTED,

        /**
         * Process stopping.
         */
        STOPPING,

        /**
         * Process stopped.
         */
        STOPPED,

        /**
         * Process canceled.
         */
        CANCELED
    }

    private final String[] command;
    private final Path dir;
    private final int maxOutputBufferLines;
    private final boolean autoPrintOutput;
    private State state = State.CREATED;
    private Process process;
    private final Lock processLock = new ReentrantLock();
    private final Condition processStateChanged = processLock.newCondition();
    private GProcessOutputCollectorTask outputCollector;
    private Integer statusCode = null;

    /**
     * Creates a process from the given command and starting directory.
     *
     * @param command Array representing the parts of the command.
     * @param dir Starting directory. If null, it will inherit the current process current directory.
     * @param maxOutputBufferLines Max lines to keep in memory from console output of this process.
     */
    public GProcessTask(final String[] command, final Path dir, final int maxOutputBufferLines) {
        this.command = command;
        this.dir = dir;
        this.maxOutputBufferLines = maxOutputBufferLines;
        this.autoPrintOutput = false;
    }

    /**
     * Creates a process from the given command and starting directory.
     *
     * @param command Array representing the parts of the command.
     * @param dir Starting directory. If null, it will inherit the current process current directory.
     * @param autoPrintOutput This convenience parameter makes it so output of the process gets continually printed to
     *                        the current process' console.
     */
    public GProcessTask(final String[] command, final Path dir, final boolean autoPrintOutput) {
        this.command = command;
        this.dir = dir;
        this.maxOutputBufferLines = autoPrintOutput ? 1_000 : 0;
        this.autoPrintOutput = autoPrintOutput;
    }

    /**
     * Convenience constructor to quickly execute a process without any concern for its output.
     *
     * @param command Array representing the parts of the command.
     * @param dir Starting directory. If null, it will inherit the current process current directory.
     */
    public GProcessTask(final String[] command, final Path dir) {
        this(command, dir, false);
    }

    /**
     * Convenience constructor to quickly execute a process without any concern for its output. It will also
     * default to the current process' current directory.
     *
     * @param command Array representing the parts of the command.
     */
    public GProcessTask(final String[] command) {
        this(command, null);
    }

    /**
     * Returns the process handle to query for other information about the process.
     *
     * @return The process handle to query for other information about the process.
     */
    public ProcessHandle getProcessHandle() {
        return GLock.lockedResult(processLock, process::toHandle);
    }

    @Override
    public void run() {
        processLock.lock();
        try {
            final ProcessBuilder pb = GOSInterface.instance.createProcessBuilder(command, dir);
            try {
                process = pb.start();
            } catch (final IOException e) {
                process = null;
                outputCollector = null;
                return;
            }
            outputCollector = new GProcessOutputCollectorTask(process, maxOutputBufferLines);
            getTaskRunner().start(outputCollector);
            setState(State.STARTED);
        } catch (final InterruptedException e) {
            setState(State.CANCELED);
            return;
        } finally {
            processLock.unlock();
            started();
        }
        try {
            if (autoPrintOutput) {
                while ((state == State.STARTED) && process.isAlive()) {
                    // We wait half a second to capture any output before printing.
                    // We are busy waiting, but I haven't figured out how else to wait for output.
                    //noinspection BusyWait
                    Thread.sleep(500);
                    printOutput();
                }
                statusCode = process.waitFor();
                printOutput();
                setState(State.STOPPED);
            } else {
                statusCode = process.waitFor();
                setState(State.STOPPED);
            }
        } catch (final InterruptedException e) {
            stop();
            setState(State.CANCELED);
        }
    }

    /**
     * Returns the status code of the finished process, or null if the process was interrupted.
     *
     * @return The status code of the finished process, or null if the process was interrupted.
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * Stops this process or any subprocess of this process.
     *
     * <p>The implementation will send a SIGINT to the given pid.
     *
     * <p>*IMPORTANT!* This is a convenience for when you need to inform a subprocess you want to exit for orderly
     * shutdown up the chain. For example, an application started by a Windows batch script will not shut down if you
     * send a SIGINT to the batch process.
     *
     * @param pid Pid of the descendant process to stop.
     */
    public void stop(final long pid) {
        processLock.lock();
        try {
            if (state != State.STARTED) {
                return;
            }
            final boolean isDescendant = process.descendants().anyMatch(processHandle -> pid == processHandle.pid());
            if (!isDescendant) {
                throw new IllegalArgumentException("pid is not a descendant of this process.");
            }
            GOSInterface.instance.sendSignalINT(pid);
            setState(State.STOPPING);
        } finally {
            processLock.unlock();
        }
    }

    /**
     * Stops the process.
     *
     * <p>The implementation will send a SIGINT.
     */
    public void stop() {
        GLock.locked(processLock, () -> stop(process.pid()));
    }

    /**
     * Gets the process state.
     *
     * @return The process state.
     */
    public State getState() {
        return GLock.lockedResult(processLock, () -> state);
    }

    private void setState(final State state) {
        GLock.locked(processLock, () -> {
            this.state = state;
            processStateChanged.signalAll();
        });
    }

    /**
     * Wait until a given state is reached or timeout expires.
     *
     * @param state Wanted state.
     * @param timeout Time to wait for state.
     * @return True if the state has been reached.
     * @throws InterruptedException If the wait was interrupted.
     */
    public boolean awaitForState(final State state, final Duration timeout) throws InterruptedException {
        return GLock.awaitConditionWithTestAndTimeout(processLock, processStateChanged,
                () -> this.state == state,
                timeout
        );
    }

    /**
     * Wait indefinitely until a given state is reached.
     *
     * @param state Wanted state.
     * @throws InterruptedException If the wait was interrupted.
     */
    public void awaitForState(final State state) throws InterruptedException {
        GLock.awaitConditionWithTest(processLock, processStateChanged,
                () -> this.state == state
        );
    }

    /**
     * Returns all collected output until this moment. Resets collection buffer.
     *
     * @return All collected lines until this moment.
     */
    public List<String> getOutput() {
        return outputCollector == null ? Collections.emptyList() : outputCollector.getOutput();
    }

    private void printOutput() {
        outputCollector.getOutput().forEach(System.out::println);
    }
}
