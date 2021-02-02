// Copyright 2020 GlitchyByte LLC
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gradle.plugin.buildinfo

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class GlitchyBuildPluginTest {

    @Test
    fun pluginRegistersTask() {
        // Create a test project and apply the plugin.
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.glitchybyte.gradle.plugin.buildinfo")
        // Verify the result.
        assertNotNull(project.tasks.findByName("saveBuildInfo"))
    }
}
