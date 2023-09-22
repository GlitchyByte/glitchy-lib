// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: MIT-0

plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

repositories {
    gradlePluginPortal()
}
