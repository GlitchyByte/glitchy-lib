// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * Log handler that redirects logs to an output stream.
 */
public class GOutputRedirectHandler extends StreamHandler {

    /**
     * Creates the log handler.
     *
     * @param outputStream Log output stream.
     * @param formatter Log formatter.
     */
    public GOutputRedirectHandler(final OutputStream outputStream, final Formatter formatter) {
        super(outputStream, formatter);
        setLevel(Level.ALL);
    }

    @Override
    public synchronized void publish(final LogRecord record) {
        super.publish(record);
        flush();
    }
}
