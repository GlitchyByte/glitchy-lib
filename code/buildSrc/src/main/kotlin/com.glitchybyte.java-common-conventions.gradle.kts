// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("java")
}

repositories {
    mavenCentral()
    if (project.plugins.hasPlugin("com.google.cloud.artifactregistry.gradle-plugin")) {
        maven {
            // Public repository.
            url = uri("artifactregistry://us-west1-maven.pkg.dev/glitchybyte-cloud/public-maven")
        }
        maven {
            // Private repository.
            url = uri("artifactregistry://us-west1-maven.pkg.dev/glitchybyte-cloud/maven")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
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

//dependencies {
//    constraints {
//        // Define dependency versions as constraints
//        implementation("org.apache.commons:commons-text:1.9")
//    }
//}
