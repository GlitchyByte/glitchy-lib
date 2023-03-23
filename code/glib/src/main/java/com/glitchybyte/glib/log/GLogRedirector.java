// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import com.glitchybyte.glib.concurrent.GConcurrentTask;
import com.glitchybyte.glib.concurrent.GEventHandler;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Log redirector.
 *
 * <p>This class reads lines from a {@code BufferedReader} and redirects them
 * as events to an event handler.
 */
public final class GLogRedirector extends GConcurrentTask {

    private final GEventHandler eventHandler;
    private final String eventKey;
    private final BufferedReader reader;

    /**
     * Creates a log redirector.
     *
     * @param eventHandler Event handler to send log line events to.
     * @param eventKey Event key.
     * @param reader {@code BufferedReader} from where to read lines.
     */
    public GLogRedirector(final GEventHandler eventHandler, final String eventKey, final BufferedReader reader) {
        super("log-redirector");
        this.eventHandler = eventHandler;
        this.eventKey = eventKey;
        this.reader = reader;
    }

    @Override
    public void run() {
        started();
        try {
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    eventHandler.send(eventKey, line);
                }
            } while (line != null);
            reader.close();
        } catch (final IOException e) {
            // We stop collecting.
        }
    }
}
