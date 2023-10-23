// Copyright 2020-2023 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("com.glitchybyte.java-library-published-conventions")
}

publishing {
    repositories {
        maven {
            // GitHub repository.
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/GlitchyByte/glitchy-kit")
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
            pom {
                name = "GLib"
                description = "Classes and utilities for general development."
                url = "https://github.com/glitchybyte/glitchy-kit"
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
    api("com.google.code.gson:gson:2.10.1")
}

// Setup build info.
group = "com.glitchybyte.glib"
version = File("../version").readLines().first().trim()
