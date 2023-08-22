// Copyright 2022-2023 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("java")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

testing {
    suites.withType(JvmTestSuite::class) {
        useJUnitJupiter("5.9.3")
    }
}

tasks.withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors().takeIf { it > 0 } ?: 1
}
