// Copyright 2020 GlitchyByte LLC
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gradle.plugin.buildinfo

/**
 * Build information.
 */
class BuildInfo(
        val group: String,    // Project group.
        val name: String,     // Project name.
        val version: String,  // Project version.
        val datetime: String, // Build datetime stamp.
        val code: String      // Build version code.
) {

    fun getJson(): String {
        // Make a map.
        val map = mapOf(
                "group" to group,
                "name" to name,
                "version" to version,
                "datetime" to datetime,
                "code" to code
        )
        // Build json.
        val sb = StringBuilder()
        sb.append("{\n")
        val count = map.size
        var index = 0
        for (pair in map) {
            sb.append("  \"${pair.key}\": \"${pair.value}\"")
            ++index
            if (index < count) {
                sb.append(",\n")
            } else {
                sb.append('\n')
            }
        }
        sb.append("}\n")
        return sb.toString()
    }

    override fun toString(): String {
        return "$group:$name:$version ($code) $datetime"
    }
}
