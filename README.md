# GlitchyLib

Included in this repository is a generic Java library and some scripts that are common to many of my projects.

---
## GLib Java library

![Version](https://img.shields.io/badge/Version-1.1.3-green) ![Java](https://img.shields.io/badge/Java-17-orange)

Various Java utility classes to bootstrap development.

[Read the javadocs!](https://glitchybyte.github.io/glitchy-lib/)

This is my personal generic library, and is meant to be copied over to a current project and expanded as the need arises. Then it's copied back, tests added, and it's ready for the next project. It used to be published, but that meant it was rarely updated.

---
## GSpring Java library

![Version](https://img.shields.io/badge/Version-1.0.0-green) ![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/SpringBoot-2.6.6-orange)

This is a library for common Spring Boot Web applications. It contains basic configurations, setup, file server endpoints, and common utils for APIs.

---
## Build code generator

`gen-code` generates a humanly readable code. Unique every second, per project, as long as it's used with the same salt. Useful to create watchers or scripts that check for change.

    ./gen-code MY_SALT

---
## Application runner

`run` builds, unpacks, and runs a Gradle project.

    ./run MY_PROJECT [ARG1 ARG2 ...]
