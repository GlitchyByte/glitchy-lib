// Copyright 2022-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.process;

import sun.misc.Signal;

/**
 * Specialization of GOSInterface for Linux and macOS.
 */
public final class GOSLinux extends GOSInterface {

    /**
     * Creates a Linux OS interface.
     */
    public GOSLinux() {
        super(GOSType.LINUX);
    }

    @Override
    public String[] getShellCommand(final String command) {
        return new String[] {
                "sh",
                "-c",
                command
        };
    }

    @Override
    public String[] getSignalCommand(final Signal signal, final long pid) {
        return new String[] { "kill", "-s", signal.getName(), Long.toString(pid) };
    }
}
