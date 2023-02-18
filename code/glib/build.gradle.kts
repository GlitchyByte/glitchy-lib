// Copyright 2020-2023 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("com.glitchybyte.java-public-published-library-conventions")
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
}

publishing {
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
    api("com.google.code.gson:gson:2.10")
}

// Setup build info.
group = "com.glitchybyte.glib"
version = "1.6.0"
