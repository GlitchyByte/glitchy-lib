// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("com.glitchybyte.java-application-conventions")
}

// Build info.
group = "com.glitchybyte.gpx"
version = "1.1.0"

application {
    // Define the main class for the application.
    mainClass.set("com.glitchybyte.gpx.App")
}

tasks.register<Jar>("standalone").configure {
    dependsOn(tasks.named("check"))
    group = "Distribution"
    description = "Creates a standalone jar."
    archiveFileName.set("${project.name}.${archiveExtension.get()}")
    manifest {
        attributes(mapOf("Main-Class" to application.mainClass))
    }
//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    isPreserveFileTimestamps = false
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks.build {
    dependsOn(tasks.named("standalone"))
}
