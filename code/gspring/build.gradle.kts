// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("com.glitchybyte.java-library-conventions")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web:2.7.2")
    api("org.apache.tika:tika-core:2.4.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.2")
}

// Setup build info.
group = "com.glitchybyte.gspring"
version = "1.0.2"
