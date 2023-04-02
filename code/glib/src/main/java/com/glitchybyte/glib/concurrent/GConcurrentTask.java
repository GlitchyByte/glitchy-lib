// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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
public abstract class GConcurrentTask implements Runnable {

    private static final AtomicLong taskCount = new AtomicLong(0);

    private GTaskRunner taskRunner = null;
    private final String taskThreadName;
    private final AtomicBoolean hasStarted = new AtomicBoolean(false);
    private final Lock hasStartedLock = new ReentrantLock();
    private final Condition hasStartedSignal = hasStartedLock.newCondition();

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
     * Creates a concurrent task with the given name, or a default name if null.
     *
     * @param taskThreadName Thread name. If null, a default unique name of the form 'Task-{NUMBER}' is used.
     */
    public GConcurrentTask(final String taskThreadName) {
        this.taskThreadName = taskThreadName == null ?
                "Task-" + Long.toHexString(taskCount.incrementAndGet()) :
                taskThreadName;
    }

    /**
     * Creates a concurrent task with a default thread name.
     */
    public GConcurrentTask() {
        this(null);
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
        Thread.currentThread().setName(taskThreadName);
        GLock.signalAll(hasStartedLock, hasStartedSignal);
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
        hasStartedLock.lock();
        try {
            while (!hasStarted.get()) {
                if (!hasStartedSignal.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                    throw new IllegalStateException("Task did not start within the timeout. Did you forget to call 'started'?");
                }
            }
        } finally {
            hasStartedLock.unlock();
        }
    }
}
