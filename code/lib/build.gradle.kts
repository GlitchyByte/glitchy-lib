// Copyright 2020-2024 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("glitchybyte.java-library-published-conventions")
}

tasks.javadoc {
    title = "GLib v$version API"
}

publishing {
    repositories {
        maven {
            // GitHub repository.
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/GlitchyByte/glitchy-lib")
            credentials {
                username = project.findProperty("gpr.username") as String?
                password = project.findProperty("gpr.token") as String?
            }
            metadataSources {
                gradleMetadata()
            }
        }
    }
    publications {
        create<MavenPublication>("GLib") {
            from(components["java"])
            artifactId = "glib"
            pom {
                name = "GLib"
                description = "Classes and utilities for general development."
                url = "https://github.com/glitchybyte/glitchy-lib"
                licenses {
                    license {
                        name = "Apache License, Version 2.0"
                        url = "https://opensource.org/licenses/Apache-2.0"
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["GLib"])
}

dependencies {
    api("com.google.code.gson:gson:2.11.0")
}

// Setup build info.
group = "com.glitchybyte"
version = File("../version").readLines().first().trim()
