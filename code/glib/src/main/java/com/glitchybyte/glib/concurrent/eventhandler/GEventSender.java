// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.eventhandler;

/**
 * Event sender.
 */
public interface GEventSender {

    /**
     * Send event.
     *
     * @param event Event object.
     */
    void send(final GEvent event);

    /**
     * Send event.
     *
     * @param key Event key.
     * @param data Event data.
     */
    void send(final String key, final Object data);

    /**
     * Send event with no data.
     *
     * @param key Event key.
     */
    void send(final String key);
}
