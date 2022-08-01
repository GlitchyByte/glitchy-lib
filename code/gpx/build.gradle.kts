// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

plugins {
    application
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.8.2")
        }
    }
}

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

dependencies {
    // None.
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
