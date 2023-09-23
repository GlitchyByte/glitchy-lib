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
                name.set("GLib")
                description.set("Classes and utilities for general development.")
                url.set("https://github.com/glitchybyte/glitchy-kit")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
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
version = "1.8.0-SNAPSHOT"
