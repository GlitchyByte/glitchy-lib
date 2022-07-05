// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gpx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public final class App implements Callable<Integer> {

    private static OSSpecific osSpecific;

    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println("gpx <GRADLE_ROOT_DIR> <GRADLE_PROJECT>");
            System.exit(1);
        }
        osSpecific = OSSpecific.isWindowsOS() ? new WindowsSpecific() : new LinuxSpecific();
        final Path dir = osSpecific.resolvedDir(args[0]);
        final String projectName = args[1].charAt(0) == ':' ? args[1] : ":" + args[1];
        final App app = new App(dir, projectName);
        final int exitCode = app.call();
        System.exit(exitCode);
    }

    private final Path gradleRootDir;
    private final String projectName;

    private App(final Path gradleRootDir, final String projectName) {
        this.gradleRootDir = gradleRootDir;
        this.projectName = projectName;
    }

    @Override
    public Integer call() {
        final int verificationCode = verify();
        if (verificationCode != 0) {
            return verificationCode;
        }
        final List<String> output = gradleBuildAndGetProperties();
        if (output == null) {
            System.err.printf(Locale.US, "Error building and getting properties for '%s'.%n", projectName);
            return 2;
        }
        final String buildDir = getPropertyValue(output, "buildDir");
        if (buildDir == null) {
            printPropertyNotFound("buildDir");
            return 2;
        }
        final String distsDirName = getPropertyValue(output, "distsDirName");
        if (distsDirName == null) {
            printPropertyNotFound("distsDirName");
            return 2;
        }
        final String applicationName = getPropertyValue(output, "applicationName");
        if (applicationName == null) {
            printPropertyNotFound("applicationName");
            return 2;
        }
        final String version = getPropertyValue(output, "version");
        final String tarName = (version == null ? applicationName : applicationName + "-" + version) + ".tar";
        final Path distDir = Paths.get(buildDir, distsDirName);
        final Path tarPath = distDir.resolve(tarName);
        if (!tarPath.toFile().isFile()) {
            System.err.println("Distribution not found: " + tarPath);
            return 2;
        }
        final Path workDir = Paths.get(buildDir, distsDirName, tarName.substring(0, tarName.length() - 4));
        deleteDir(workDir);
        osSpecific.execute("tar -xf " + tarPath, distDir);
        // Exit.
        final Path binPath = workDir.resolve("bin").resolve(applicationName);
        System.out.printf(Locale.US, "%s%s%n", binPath, OSSpecific.isWindowsOS() ? ".bat" : "");
        return 0;
    }

    private int verify() {
        final File gradleScript = gradleRootDir.resolve("gradlew").toFile();
        if (!gradleScript.isFile()) {
            System.out.println("Not a Gradle directory: " + gradleRootDir);
            return 1;
        }
        return 0;
    }

    private void printPropertyNotFound(final String name) {
        System.err.printf(Locale.US, "Property '%s' not found!", name);
    }

    private List<String> gradleBuildAndGetProperties() {
        final String gradleArgs = String.format(Locale.US, "%1$s:build %1$s:properties", projectName);
        return osSpecific.executeGradle(gradleArgs, gradleRootDir);
    }

    private String getPropertyValue(final List<String> properties, final String key) {
        final String fullKey = key + ": ";
        final String value = properties.stream().filter(line -> line.startsWith(fullKey)).findFirst().orElse(null);
        return value == null ? null : value.substring(fullKey.length());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteDir(final Path dir) {
        if (!dir.toFile().exists()) {
            return;
        }
        try (final var pathStream = Files.walk(dir)) {
            pathStream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
