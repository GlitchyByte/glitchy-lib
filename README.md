# GlitchyLib

## GLib Java library

Various Java utility classes to bootstrap development.

[Read the javadocs!](https://glitchybyte.github.io/glitchy-lib/)

This is my personal generic library, and is meant to be copied over to a current project and expanded as the need arises. Then it's copied back, tests added, and it's ready for the next project. It used to be published, but that meant it was rarely updated.

---
## Build code generator

`gen-code` generates a humanly readable code. Unique per project as long as it's used with the same salt. Useful to create watchers or scripts that check for change.

    ./gen-code MY_SALT
