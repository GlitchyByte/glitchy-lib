// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

/**
 * A work queue system. Very low contention queueing of work. Options to
 * process work sequentially or in parallel. Each work item processing happens
 * on its own thread, giving the implementor freedom to do heavy lifting code
 * directly on the processor function without devising other mechanisms to free
 * the thread fast.
 */
package com.glitchybyte.glib.concurrent.workqueue;
