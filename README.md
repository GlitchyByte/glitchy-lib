# GlitchyLib

Included in this repository is a generic Java library and some scripts that are common to many of my projects.

---
## GLib Java library

Various Java utility classes to bootstrap development.

[Read the javadocs!](https://glitchybyte.github.io/glitchy-lib/)

This is my personal generic library, and is meant to be copied over to a current project and expanded as the need arises. Then it's copied back, tests added, and it's ready for the next project. It used to be published, but that meant it was rarely updated.

---
## GSpring Java library

This is a new project. Not sure it will stay, but it's here for now. It contains common configurations and setup for Spring Boot Web applications.

*Documentation is non-existent at the moment. I'll add more as it matures.*

---
## Build code generator

`gen-code` generates a humanly readable code. Unique per project as long as it's used with the same salt. Useful to create watchers or scripts that check for change.

    ./gen-code MY_SALT

---
## Application runner

`run` builds, unpacks, and runs a Gradle project.

    ./run MY_PROJECT [ARG1 ARG2 ...]
