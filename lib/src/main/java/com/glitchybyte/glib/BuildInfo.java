// Copyright 2020 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Build information.
 * <p>
 * Retrieves building information from Gradle generated file.
 */
public final class BuildInfo {

    /**
     * Default name for build information file, as per plugin.
     */
    public static final String DEFAULT_BUILD_INFO = "build-info.json";

    /**
     * Loads build information from the given stream.
     *
     * @param stream InputStream to read info from.
     * @return A fully populated {@code BuildInfo}.
     * @throws IOException If there is a problem reading from stream.
     */
    public static BuildInfo loadFromStream(final InputStream stream) throws IOException {
        try (final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return GJson.fromReader(reader, BuildInfo.class);
        }
    }

    /**
     * Loads build information from the given path.
     *
     * @param path Path to information file.
     * @return A fully populated {@code BuildInfo}.
     * @throws IOException If there is a problem reading from path.
     */
    public static BuildInfo loadFromPath(final Path path) throws IOException {
        return loadFromStream(Files.newInputStream(path));
    }

    /**
     * Loads build information from the given file at the given directory.
     *
     * @param directory Directory where build information file resides.
     * @param buildInfoFilename Name of build information file.
     * @return A fully populated {@code BuildInfo}.
     * @throws IOException If there is a problem reading from path.
     */
    public static BuildInfo loadFromDirectory(final Path directory, final String buildInfoFilename) throws IOException {
        return loadFromPath(directory.resolve(buildInfoFilename));
    }

    /**
     * Loads build information from the default file at the given directory.
     *
     * @param directory Directory where build information file resides.
     * @return A fully populated {@code BuildInfo}.
     * @throws IOException If there is a problem reading from path.
     */
    public static BuildInfo loadFromDirectory(final Path directory) throws IOException {
        return loadFromDirectory(directory, DEFAULT_BUILD_INFO);
    }

    /**
     * Loads build information from the given resource path.
     *
     * @param tClass Class to use loader from.
     * @param resourcePath Path to build information file.
     * @return A fully populated {@code BuildInfo}.
     * @throws IOException If there is a problem reading from resource.
     */
    public static BuildInfo loadFromResource(final Class<?> tClass, final String resourcePath) throws IOException {
        return loadFromStream(tClass.getResourceAsStream(resourcePath));
    }

    /**
     * Load build information from the default file in the package from the class given.
     *
     * @param tClass Class to extract package source, and to use loader from.
     * @return A fully populated {@code BuildInfo}.
     * @throws IOException If there is a problem reading from resource.
     */
    public static BuildInfo loadFromResource(final Class<?> tClass) throws IOException {
        return loadFromStream(tClass.getResourceAsStream(DEFAULT_BUILD_INFO));
    }

    /**
     * Project group.
     */
    public final String group;

    /**
     * Project name.
     */
    public final String name;

    /**
     * Project version.
     */
    public final String version;

    /**
     * Datetime stamp.
     */
    public final String datetime;

    /**
     * Build code.
     */
    public final String code;

    private BuildInfo() {
        group = null;
        name = null;
        version = null;
        datetime = null;
        code = null;
    }

    @Override
    public String toString() {
        return GStrings.format("%s:%s:%s (%s) %s", group, name, version, code, datetime);
    }
}
