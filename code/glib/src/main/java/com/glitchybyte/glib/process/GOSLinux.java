// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.process;

import sun.misc.Signal;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Specialization of GOSInterface for Linux and macOS.
 */
public final class GOSLinux extends GOSInterface {

    @Override
    public Path resolvedDir(final String dir) {
        if (dir.equals("~")) {
            return Paths.get(System.getProperty("user.home")).resolve(dir.substring(1)).normalize();
        }
        if (dir.startsWith("~/")) {
            return Paths.get(System.getProperty("user.home")).resolve(dir.substring(2)).normalize();
        }
        return Paths.get(System.getProperty("user.dir")).resolve(dir).normalize();
    }

    @Override
    public String[] getShellCommand(final String[] command) {
        final String[] shellCommand = new String[command.length + 2];
        shellCommand[0] = "sh";
        shellCommand[1] = "-c";
        System.arraycopy(command, 0, shellCommand, 2, command.length);
        return shellCommand;
    }

    @Override
    public String[] getSignalCommand(final Signal signal, final long pid) {
        return new String[] { "kill", "-s", signal.getName(), Long.toString(pid) };
    }
}
