// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A single concurrent task that starts in a separate thread and runs until
 * finished or the task is interrupted.
 *
 * <p>Implementations MUST call {@code started} within their {@code run}
 * implementation when the task is ready to receive inputs (e.g., after
 * acquiring locks). Repeated calls to {@code started} are permitted and will
 * return fast without performing any further synchronization.
 */
public abstract class GTask implements Runnable {

    private GTaskRunner taskRunner = null;
    private Thread taskThread = null;
    private final String taskThreadName;
    private final AtomicBoolean hasStarted = new AtomicBoolean(false);
    private final Lock hasStartedLock = new ReentrantLock();
    private final Condition hasStartedSignal = hasStartedLock.newCondition();
    private final AtomicBoolean isDone = new AtomicBoolean(false);
    private final Lock isDoneLock = new ReentrantLock();
    private final Condition isDoneSignal = isDoneLock.newCondition();

    /**
     * Creates a concurrent task with the given name, or a default name if null.
     *
     * @param taskThreadName Thread name. If null, a default unique name of the form 'Task-{NUMBER}' is used.
     */
    public GTask(final String taskThreadName) {
        this.taskThreadName = taskThreadName;
    }

    /**
     * Creates a concurrent task with a default thread name.
     */
    public GTask() {
        this(null);
    }

    /**
     * Sets the task runner this task can use to run other tasks.
     * On start, it defaults to the runner that is running this task.
     *
     * @param taskRunner Task runner.
     */
    protected void setTaskRunner(final GTaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    /**
     * Returns a task runner this task can use to run other tasks.
     * On start, it defaults to the runner that is running this task.
     *
     * @return A task runner this task can use to run other tasks.
     */
    protected GTaskRunner getTaskRunner() {
        return taskRunner;
    }

    /**
     * Returns the given name of this task thread, or null if it wasn't set.
     * If name is not set the runner will assign a default name when creating
     * the thread.
     *
     * @return The given name of this task thread.
     */
    public String getTaskThreadName() {
        return taskThreadName;
    }

    /**
     * Marks this task as started, allowing the queueing thread to continue.
     *
     * <p>This method MUST be called to indicate the task is running and ready.
     * Even if the task decides it will exit fast it must call this method.
     *
     * <p>Repeated calls to this method are permitted and will return fast
     * without performing any further synchronization.
     */
    protected void started() {
        if (!hasStarted.compareAndSet(false, true)) {
            return;
        }
        taskThread = Thread.currentThread();
        GLock.signalAll(hasStartedLock, hasStartedSignal);
    }

    void done() {
        isDone.compareAndSet(false, true);
        GLock.signalAll(isDoneLock, isDoneSignal);
    }

    /**
     * Returns whether this task has completed.
     *
     * @return Whether this task has completed.
     */
    public boolean isDone() {
        return isDone.get();
    }

    /**
     * Awaits indefinitely until the task is done.
     *
     * @throws InterruptedException If the wait was interrupted.
     */
    public void awaitDone() throws InterruptedException {
        GLock.awaitConditionWithTest(isDoneLock, isDoneSignal, this::isDone);
    }

    /**
     * Interrupts the task.
     */
    public void interrupt() {
        taskThread.interrupt();
    }

    /**
     * Awaits for the task to start.
     *
     * <p>If the task takes longer to start than the given timeout an
     * {@code IllegalStateException} will be raised as this is considered a
     * bug in the task implementation.
     *
     * @param timeout Time to wait for task to start before considering it a failure.
     * @throws InterruptedException If the wait is interrupted.
     */
    void awaitStarted(final Duration timeout) throws InterruptedException {
        if (!GLock.awaitConditionWithTestAndTimeout(hasStartedLock, hasStartedSignal, hasStarted::get, timeout)) {
            throw new IllegalStateException("Task did not start within the timeout. Did you forget to call 'started'?");
        }
    }
}
