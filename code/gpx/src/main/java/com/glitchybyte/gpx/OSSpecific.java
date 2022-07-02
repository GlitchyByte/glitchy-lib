// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gpx;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class OSSpecific {

    public static boolean isWindowsOS() {
        final String osName = System.getProperty("os.name").toLowerCase(Locale.US);
        return osName.contains("windows");
    }

    public abstract Path getResolvedDir(final String dir);

    public List<String> execute(final String command, final Path dir) {
        final ProcessBuilder pb = new ProcessBuilder()
                .directory(dir.toFile());
        setProcessBuilderShell(pb, command);
        try {
            final Process process = pb.start();
            final List<String> output;
            try (final BufferedReader reader = process.inputReader(StandardCharsets.UTF_8)) {
                output = reader.lines().collect(Collectors.toList());
            }
            return process.waitFor() == 0 ? output : null;
        } catch (final IOException | InterruptedException e) {
            return null;
        }
    }

    protected abstract void setProcessBuilderShell(final ProcessBuilder pb, final String command);

    public List<String> executeGradle(final String task, final Path dir) {
        final String command = String.format(Locale.US, "%s %s", getGradleScript(), task);
        return execute(command, dir);
    }

    protected abstract String getGradleScript();
}
