// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * Standard logger console handler.
 *
 * <p>Default level is {@code ALL}.
 */
public class GStandardConsoleHandler extends ConsoleHandler {

    /**
     * Creates a standard logger console handler.
     */
    public GStandardConsoleHandler() {
        setFormatter(new GStandardLogFormatter());
        setLevel(Level.ALL);
    }
}
