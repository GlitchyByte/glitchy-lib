// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gpx;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class LinuxSpecific extends OSSpecific {

    @Override
    public Path getResolvedDir(final String dir) {
        if (dir.equals("~")) {
            return Paths.get(System.getProperty("user.home")).resolve(dir.substring(1)).normalize();
        }
        if (dir.startsWith("~/")) {
            return Paths.get(System.getProperty("user.home")).resolve(dir.substring(2)).normalize();
        }
        return Paths.get(System.getProperty("user.dir")).resolve(dir).normalize();
    }

    @Override
    protected void setProcessBuilderShell(final ProcessBuilder pb, final String command) {
        pb.command("sh", "-c", command);
    }

    @Override
    protected String getGradleScript() {
        return "./gradlew";
    }
}
