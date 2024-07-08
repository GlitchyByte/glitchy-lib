# glib

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
    implementation("com.glitchybyte:glib:1.10.1")
}
```

Notable changes:

* Changed (shortened) maven address.
