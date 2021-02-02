// Copyright 2020-2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

import java.util.Locale
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
    id("com.glitchybyte.gradle.plugin.buildinfo")
    `maven-publish`
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.1.1"
}

repositories {
//    maven(url = uri("artifactregistry://us-maven.pkg.dev/glitchy-maven/repo"))
//    mavenLocal()
    mavenCentral()
}

tasks {
    saveBuildInfo {
        useRootName = true
        codeBitXor = 0xb5b0c7e8
        destinations = setOf(
            "$projectDir/src/main/resources/com/glitchybyte/lib"
        )
    }
}

java {
    // Java version for the library.
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
    // Create javadoc and sources publishing artifacts.
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Test> {
    // Use JUnit 5 (Jupiter) with 8 parallel test execution and full logging.
    useJUnitPlatform()
    maxParallelForks = 8
    testLogging.exceptionFormat = TestExceptionFormat.FULL
}

configurations.all {
    resolutionStrategy {
        failOnNonReproducibleResolution()
    }
}

dependencies {
    // Main dependencies.
    implementation("com.google.code.gson:gson:2.8.6")
    // Test dependencies.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

// Setup build info.
group = "com.glitchybyte"
version = "1.0.0"

// Set the name of the artifacts to that of the root project.
tasks.withType<Jar> {
    archiveBaseName.set(rootProject.name)
}

// Expose javadoc for GitHub.
tasks.register<Copy>("exposeJavadoc") {
    dependsOn("javadoc")
    from("$buildDir/docs/javadoc")
    into("$rootDir/docs")
}

tasks.named("javadoc") {
    finalizedBy("exposeJavadoc")
}

// Publish to Google Cloud Platform Artifact Registry.
publishing {
    repositories {
        maven(url = uri("artifactregistry://us-maven.pkg.dev/glitchy-maven/repo"))
    }
}

publishing {
    publications {
        create<MavenPublication>("library") {
            groupId = project.group as String
            artifactId = rootProject.name.toLowerCase(Locale.US)
            version = project.version as String
            from(components["java"])
            pom {
                name.set("Glitchy Library")
                description.set("Various utilities to bootstrap development.")
                url.set("https://github.com/glitchybyte/glitchy-lib")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}
