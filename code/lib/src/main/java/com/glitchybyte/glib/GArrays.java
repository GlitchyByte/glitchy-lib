// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import java.util.function.Consumer;

/**
 * Array utilities.
 */
public final class GArrays {

    /**
     * Iterates over the array and passes each element to the given consumer.
     *
     * @param array Array to iterate over.
     * @param consumer Element consumer.
     * @param <T> Type of array elements.
     */
    public static <T> void forEach(final T[] array, final Consumer<T> consumer) {
        for (final T item: array) {
            consumer.accept(item);
        }
    }

    private GArrays() {
        // Hiding constructor.
    }
}
