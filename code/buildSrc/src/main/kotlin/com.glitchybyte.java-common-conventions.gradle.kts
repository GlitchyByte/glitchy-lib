// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

testing {
    suites.withType(JvmTestSuite::class) {
        useJUnitJupiter("5.8.2")
    }
}

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

/*
dependencies {
    constraints {
        // Define dependency versions as constraints
        implementation("org.apache.commons:commons-text:1.9")
    }
}
*/
