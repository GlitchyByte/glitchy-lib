// Copyright 2020 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.lib;

import java.io.IOException;

/**
 * Build information for GlitchyLib.
 */
public final class GlitchyLibBuildInfo {

    /**
     * Returns build information for GlitchyLib.
     *
     * @return Build information for GlitchyLib.
     */
    public static BuildInfo get() {
        try {
            return BuildInfo.loadFromResource(GlitchyLibBuildInfo.class);
        } catch (final IOException e) {
            return null;
        }
    }

    private GlitchyLibBuildInfo() {
        // Hiding constructor.
    }
}
