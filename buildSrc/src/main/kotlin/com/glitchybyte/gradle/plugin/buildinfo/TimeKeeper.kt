// Copyright 2020 GlitchyByte LLC
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gradle.plugin.buildinfo

import java.time.Duration
import java.time.Instant

/**
 * Basic time keeping class to have a good starting time for future builds.
 */
class TimeKeeper(
        private val zeroInstant: Instant // Zero time marker.
) {

    /**
     * Returns seconds since zero instant.
     */
    fun getSeconds(): Long {
        val now = Instant.now()
        val duration = Duration.between(zeroInstant, now)
        return duration.toSeconds()
    }
}
