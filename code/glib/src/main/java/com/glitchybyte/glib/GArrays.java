// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import java.util.function.Consumer;

/**
 * Array utilities.
 */
public final class GArrays {

    /**
     * Creates an array of the given size and of the given inferred element type.
     *
     * @param size Size of the new array.
     * @return A new array.
     * @param <T> Type of array elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(final int size) {
        return (T[]) new Object[size];
    }

    /**
     * Iterates over each element of the array and passes each to the given consumer.
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
