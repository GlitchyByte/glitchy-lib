// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * Color logger console handler.
 *
 * <p>Default level is {@code ALL}.
 */
public class GColorConsoleHandler extends ConsoleHandler {

    /**
     * Creates a color logger console handler.
     */
    public GColorConsoleHandler() {
        setFormatter(new GColorLogFormatter());
        setLevel(Level.ALL);
    }
}
