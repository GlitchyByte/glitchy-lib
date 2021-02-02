// Copyright 2020 GlitchyByte LLC
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gradle.plugin.buildinfo

import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Functional test.
 */
class BuildInfoPluginFunctionalTest {

    @Test
    fun canRunTask() {
        // Setup build.
        val projectDir = File("build/functionalTest")
        projectDir.mkdirs()
        projectDir.resolve("settings.gradle").writeText("rootProject.name = \"MyFunctionalTest\"")
        projectDir.resolve("build.gradle").writeText("""
            plugins {
                id('com.glitchybyte.gradle.plugin.buildinfo')
            }
            group = "myGroup"
            version = "5.0.8"
            tasks {
                saveBuildInfo {
                    destinations = Set.of("build/functionalTest")
                }
            }
        """)
        // Run build.
        val runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withArguments("saveBuildInfo")
                .withProjectDir(projectDir)
        val result = runner.build()
        // Verify results.
        assertTrue(result.output.contains("BuildInfo: myGroup:MyFunctionalTest:5.0.8"))
        assertTrue(Files.isRegularFile(Path.of("build/functionalTest/build-info.json")))
    }
}
