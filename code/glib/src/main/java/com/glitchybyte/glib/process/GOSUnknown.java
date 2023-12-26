// Copyright 2022-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.process;

import sun.misc.Signal;

/**
 * Specialization of GOSInterface for unknown OS. It will throw exceptions on every call.
 */
public final class GOSUnknown extends GOSInterface {

    /**
     * Creates an unknown OS interface.
     */
    public GOSUnknown() {
        super(GOSType.UNKNOWN);
    }

    @Override
    public String[] getShellCommand(final String command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSignalCommand(final Signal signal, final long pid) {
        throw new UnsupportedOperationException();
    }
}
