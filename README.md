# GlitchyLib

Included in this repository is a general purpose Java library and some scripts that are common to many of my projects.

---
## GLib Java library

![Version](https://img.shields.io/badge/Version-1.3.0-blue) ![Java](https://img.shields.io/badge/Java-17-orange)

Various Java utility classes to bootstrap development.

[Read the javadocs!](https://glitchybyte.github.io/glitchy-lib/)

This is my personal general purpose library. It is meant to be copied over to a current project and expanded as the need arises. Then it's copied back, tests added, and it's ready for the next project. It used to be published, but that meant it was rarely updated.

---
## GSpring Java library

![Version](https://img.shields.io/badge/Version-1.0.2-blue) ![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/SpringBoot-2.7.2-orange)

This is a library for common Spring Boot Web applications. It contains basic configurations, setup, file server endpoints, and common utils for APIs.

---
## Build code generator

`gen-code` generates a humanly readable code. Unique every second, per project, as long as it's used with the same salt. Useful to create watchers or scripts that check for change.

    ./gen-code MY_SALT

---
## Application runner

![Version](https://img.shields.io/badge/Version-1.1.0-blue) ![Java](https://img.shields.io/badge/Java-17-orange)

Builds, unpacks, and runs a Gradle project on the actual shell, as opposed to the Gradle run task which doesn't attach a proper console.

Though particular to my project directory structures, it could be easily modified to suit other needs. This runner works on macOS, Linux, and Windows.

To use, copy the `run` directory to the root of the project (in my case the directory containing the `code` directory).

### macOS or Linux:

    run/run MY_PROJECT [ARG1 ARG2 ...]

### Windows:

    run\run.bat MY_PROJECT [ARG1 ARG2 ...]
