// Copyright 2020 GlitchyByte LLC
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gradle.plugin.buildinfo

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin to autogenerate a json file with build information.
 * <p>
 * The generated fields are:
 * <pre>
 *     group: Project group (e.g., com.glitchybyte)
 *     name: Project name (e.g., MyAwesomeApp)
 *     version: Project version (e.g., 1.0.0)
 *     datetime: Date and time of build (e.g., 20201127102733)
 *     code: Unique code for this build (e.g., d67x0j)
 * </pre>
 */
class BuildInfoPlugin: Plugin<Project> {

    /**
     * Apply this plugin to the given project.
     *
     * @param project The project.
     */
    override fun apply(project: Project) {
        // Add Java plugin.
        project.plugins.apply("java")
        // Add task and extension for configuration.
        val extension: BuildInfoPluginExtension = project.extensions.create("saveBuildInfo", BuildInfoPluginExtension::class.java)
        val saveBuildInfoTask  = project.tasks.register("saveBuildInfo", SaveBuildInfoTask::class.java, extension)
        // Set as a dependency of processResources so it's ready with other resources.
        project.tasks.named("processResources") { task ->
            task.dependsOn(saveBuildInfoTask)
        }
    }
}
