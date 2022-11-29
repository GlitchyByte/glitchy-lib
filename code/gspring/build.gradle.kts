// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("com.glitchybyte.java-public-published-library-conventions")
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
}

publishing {
    publications {
        create<MavenPublication>("GSpring") {
            from(components["java"])
            pom {
                name.set("GSpring")
                description.set("Setup and utilities for API development with Spring Framework.")
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
    sign(publishing.publications["GSpring"])
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web:3.0.0")
    api("org.apache.tika:tika-core:2.6.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.0")
}

// Setup build info.
group = "com.glitchybyte.gspring"
version = "1.1.0"
