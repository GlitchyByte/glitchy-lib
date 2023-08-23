// Copyright 2022-2023 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven {
        // GitHub repository.
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/GlitchyByte/*")
        credentials {
            username = project.findProperty("gpr.username") as String?
            password = project.findProperty("gpr.token") as String?
        }
        metadataSources {
            gradleMetadata()
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

tasks.compileJava {
    // Add version file to resources.
    doLast {
        val versionFile = project.file("src/main/resources/version")
        versionFile.writeText(project.version.toString())
    }
}

testing {
    suites.withType(JvmTestSuite::class) {
        useJUnitJupiter("5.9.3")
    }
}

tasks.withType<Test>().configureEach {
    // Maximize parallel forks.
    maxParallelForks = Runtime.getRuntime().availableProcessors().takeIf { it > 0 } ?: 1
}
