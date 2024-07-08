// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent.event;

import com.glitchybyte.glib.GObjects;

/**
 * Event.
 */
public final class GEvent {

    /**
     * Event type.
     */
    public final String type;

    /**
     * Event data.
     */
    public final Object data;

    /**
     * Creates an event.
     *
     * @param type Event type.
     * @param data Event data.
     */
    public GEvent(final String type, final Object data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Returns data as the given type of object.
     *
     * @param tClass Data type.
     * @return Data as the given type of object.
     * @param <T> Type of data.
     */
    public <T> T getDataAs(final Class<T> tClass) {
        return GObjects.castOrNull(data, tClass);
    }

    /**
     * Returns data as an {@code int}.
     *
     * @return Data as an {@code int}.
     */
    public int getDataAsInt() {
        return getDataAs(Integer.class);
    }

    /**
     * Returns data as an {@code long}.
     *
     * @return Data as an {@code long}.
     */
    public long getDataAsLong() {
        return getDataAs(Long.class);
    }

    /**
     * Returns data as an {@code float}.
     *
     * @return Data as an {@code float}.
     */
    public float getDataAsFloat() {
        return getDataAs(Float.class);
    }

    /**
     * Returns data as an {@code double}.
     *
     * @return Data as an {@code double}.
     */
    public double getDataAsDouble() {
        return getDataAs(Double.class);
    }

    /**
     * Returns data as an {@code String}.
     *
     * @return Data as an {@code String}.
     */
    public String getDataAsString() {
        return getDataAs(String.class);
    }
}
