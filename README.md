# Glitchy Kit

![Java](https://img.shields.io/badge/Java-21-orange)

Included in this repository are libraries and utilities common to many of my projects.

---
## GLib Java library
![Version](https://img.shields.io/badge/Version-1.8.1-blue)

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
    implementation("com.glitchybyte.glib:glib:1.8.1")
}
```

---
## Build code generator
![Version](https://img.shields.io/badge/Version-1.0.1-blue)

`gen-code` is a bash script to generate a humanly readable code. Unique every second, per project, as long as it's used with the same salt. Useful to create watchers or scripts that check for change.

    ./gen-code MY_SALT

To use, copy `gen-code` script, found in the `artifacts` directory, directly into your solution and call it from your build scripts as needed. The file is small enough to be committed as part of your solution.

```bash
# You can output to a file:
./gen-code MY_SALT > build-code.txt

# Or assign it to a variable:
my_var=$(./gen-code MY_SALT)
```
