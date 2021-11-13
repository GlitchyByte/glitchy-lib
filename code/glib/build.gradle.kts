// Copyright 2020-2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    maxParallelForks = 4
}

dependencies {
    // Main dependencies.
    api("com.google.code.gson:gson:2.8.9")
    // Test dependencies.
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

// Setup build info.
group = "com.glitchybyte.glib"
version = "1.0.0"

tasks.named<Javadoc>("javadoc") {
    title = "${rootProject.name} v${version} API"
}

// ---- Generate build info ----

//import java.io.ByteArrayOutputStream
//import java.nio.file.Paths
//import java.nio.file.Files
//
//tasks.named<ProcessResources>("processResources") {
//    dependsOn("generateBuildInfo")
//}
//
//tasks.register("generateBuildInfo") {
//    val outputStream = ByteArrayOutputStream()
//    exec {
//        executable("${rootDir}/../gen-code")
//        args("${project.group}")
//        standardOutput = outputStream
//    }
//    val buildInfo = """
//        {
//            "group": "${project.group}",
//            "version": "${project.version}",
//            "code": "${outputStream.toString().trim()}"
//        }
//
//    """.trimIndent()
//    val path = Paths.get("${projectDir}/src/main/resources/" + "${project.group}".replace('.', '/') + "/build-info.json")
//    val parentPath = path.parent
//    Files.createDirectories(parentPath)
//    path.toFile().writeText(buildInfo)
//}
