// Copyright 2020-2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.wrapped;

/**
 * Wrapper around a String.
 */
public class GWrappedString extends GWrappedObject<String> {

    public GWrappedString(final String value) {
        super(value);
    }

    public GWrappedString() {
        super();
    }
}
