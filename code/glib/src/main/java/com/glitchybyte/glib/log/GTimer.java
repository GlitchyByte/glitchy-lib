// Copyright 2020-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import com.glitchybyte.glib.GStrings;
import com.glitchybyte.glib.log.GLog;

import java.time.Duration;
import java.time.Instant;

/**
 * Timer class for on-the-fly quick profiling.
 * This DOES NOT replace a real profiler.
 */
public final class GTimer {

    private final Instant startTime;

    /**
     * Creates a timer that starts right now!
     */
    public GTimer() {
        startTime = Instant.now();
    }

    /**
     * Logs current elapsed time and a message.
     *
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public void print(final String msg, final Object... params) {
        final double elapsed = (double) Duration.between(startTime, Instant.now()).toNanos() / 1_000_000;
        final String timer = GStrings.format("(%.3f) ", elapsed);
        GLog.info(timer + msg, params);
    }
}
