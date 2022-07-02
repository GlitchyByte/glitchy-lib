// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

plugins {
    `java-library`
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
    api("org.springframework.boot:spring-boot-starter-web:2.7.0")
    api("org.apache.tika:tika-core:2.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.0")
}

// Setup build info.
group = "com.glitchybyte.gspring"
version = "1.0.1"
