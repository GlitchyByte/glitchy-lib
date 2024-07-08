// Copyright 2014-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

/**
 * Object utilities.
 */
public final class GObjects {

    /**
     * Tests if {@code obj} is of type {@code tClass} and returns the same object of that type.
     * If it's not of the type, it returns null.
     *
     * @param obj Object to test and return.
     * @param tClass Class we want the object to be.
     * @param <T> Type of returned object.
     * @return {@code obj} cast as {@code tClass} or null.
     */
    public static <T> T castOrNull(final Object obj, final Class<T> tClass) {
        if (obj == null) {
            return null;
        }
        return tClass.isInstance(obj) ? tClass.cast(obj) : null;
    }

    private GObjects() {
        // Hiding constructor.
    }
}
