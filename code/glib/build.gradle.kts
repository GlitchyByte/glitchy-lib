// Copyright 2020-2022 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    id("com.glitchybyte.java-library-conventions")
}

dependencies {
    api("com.google.code.gson:gson:2.9.0")
}

// Setup build info.
group = "com.glitchybyte.glib"
version = "1.3.0"

tasks.named<Javadoc>("javadoc") {
    title = "${rootProject.name} v${version} API"
}
