// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.eventhandler;

import java.util.function.Consumer;

/**
 * Event handler.
 */
public interface GEventHandler extends GEventSender {

    /**
     * Registers a handler to the given key.
     *
     * @param key Event key.
     * @param handler Event handler.
     */
    void registerHandler(final String key, final Consumer<GEvent> handler);
}
