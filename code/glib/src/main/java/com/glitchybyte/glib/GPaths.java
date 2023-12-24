// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import java.nio.file.Path;

/**
 * Path utilities.
 */
public final class GPaths {

    /**
     * Returns the current working directory.
     *
     * @return The current working directory.
     */
    public static Path getCurrentDirectory() {
        final String property = System.getProperty("user.dir");
        return property == null ? null : Path.of(property);
    }

    /**
     * Returns the current user home directory.
     *
     * @return The current user home directory.
     */
    public static Path getHomeDirectory() {
        final String property = System.getProperty("user.home");
        return property == null ? null : Path.of(property);
    }

    /**
     * Returns the absolute and normalized path.
     *
     * @param path Path to absolutize and normalize.
     * @return The absolute and normalized path.
     */
    public static Path getFullPath(final Path path) {
        return path.toAbsolutePath().normalize();
    }

    /**
     * Returns the absolute and normalized path.
     * Also, expands "~" to the user home directory.
     *
     * @param path Path to absolutize and normalize.
     * @return The absolute and normalized path.
     */
    public static Path getFullPath(final String path) {
        if (path.startsWith("~/")) {
            final Path home = getHomeDirectory();
            return home == null ? null : getFullPath(getHomeDirectory().resolve(path.substring(2)));
        }
        return getFullPath(Path.of(path));
    }

    private GPaths() {
        // Hiding constructor.
    }
}
