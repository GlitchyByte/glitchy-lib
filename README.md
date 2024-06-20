# Glitchy-Lib Java library

![Version](https://img.shields.io/badge/Version-1.10.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)

Classes and utilities for general development.
[Read the javadoc!](https://glitchybyte.github.io/glitchy-kit/)

To use in your own projects, make sure you have the appropriate credentials in your `gradle.properties`, and add the repository and dependency like this (Gradle Kotlin):

```kotlin
repositories {
    maven {
        // GitHub repository.
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/GlitchyByte/*")
        credentials {
            username = project.findProperty("gpr.username") as String?
            password = project.findProperty("gpr.token") as String?
        }
        metadataSources {
            gradleMetadata()
        }
    }
}

dependencies {
    implementation("com.glitchybyte.glib:glib:1.10.0")
}
```
