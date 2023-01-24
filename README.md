# Glitchy Kit

Included in this repository are libraries and utilities common to many of my projects.

This is a way of keeping my dependencies updated in one place and working together. It would be a bit cumbersome to keep them each in its own repository, and in sync with each other.

![Java](https://img.shields.io/badge/Java-19-orange)

---
## GLib Java library
![Version](https://img.shields.io/badge/Version-1.5.0-blue)

Classes and utilities for general development.
[Read the javadocs!](https://glitchybyte.github.io/glitchy-kit/glib/)

To use in your own projects add it like this (Gradle Kotlin):

```kotlin
plugins {
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
}

repositories {
    maven {
        url = uri("artifactregistry://us-west1-maven.pkg.dev/glitchybyte-cloud/public-maven")
    }
}

dependencies {
    implementation("com.glitchybyte.glib:glib:1.5.0")
}
```

---
## GSpring Java library
![Version](https://img.shields.io/badge/Version-1.1.1-blue) ![Spring Boot](https://img.shields.io/badge/SpringBoot-3.0.0-orange)

*ATTENTION! This library will be deprecated soon!!! I am using Helidon instead of SpringBoot now and I love it so much. And it makes 90% of this library useless.*

Setup and utilities for API development with Spring Framework.
[Read the javadocs!](https://glitchybyte.github.io/glitchy-kit/gspring/)

To use in your own projects add it like this (Gradle Kotlin):

```kotlin
plugins {
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
}

repositories {
    maven {
        url = uri("artifactregistry://us-west1-maven.pkg.dev/glitchybyte-cloud/public-maven")
    }
}

dependencies {
    implementation("com.glitchybyte.gspring:gspring:1.1.1")
}
```

---
## Build code generator
![Version](https://img.shields.io/badge/Version-1.0.0-blue)

`gen-code` is a bash script to generate a humanly readable code. Unique every second, per project, as long as it's used with the same salt. Useful to create watchers or scripts that check for change.

    ./gen-code MY_SALT

To use, copy `gen-code` script, found in the `artifacts` directory, directly into your solution and call it from your build scripts as needed. The file is small enough to be committed as part of your solution.

```bash
# You can output to a file:
./gen-code MY_SALT > build-code.txt

# Or assign it to a variable:
my_var=$(./gen-code MY_SALT)
```

---
## Application runner
![Version](https://img.shields.io/badge/Version-1.2.0-blue)

Builds, unpacks, and runs a Gradle project on the current console, as opposed to the Gradle run task which doesn't attach a proper console. This runner works on macOS, Linux, and Windows.

    ./run/run GRADLE_ROOT MY_PROJECT [ARG1 ARG2 ...]

To use, copy the `run` directory, found in the `artifacts` directory, to the root of your solution. The directory and its contents are small enough to be committed as part of your solution.

```bash
# For example: my Gradle root is in the "code" directory withing my overall "solution" directory. The project I want to run is "say":
# /solution
#   /code
#     /say
#   /run
./run/run code say "Hello, world!"
```
