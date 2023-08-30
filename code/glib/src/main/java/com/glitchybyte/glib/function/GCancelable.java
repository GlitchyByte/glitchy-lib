// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.function;

/**
 * Represents a function that cancels a task.
 */
@FunctionalInterface
public interface GCancelable {

    /**
     * Cancels the task.
     */
    void cancel();
}
