// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.process;

import java.util.List;

/**
 * Process result.
 */
public final class GProcessResult {

    /**
     * Process output.
     */
    public final List<String> output;

    /**
     * Process exit code.
     */
    public final Integer exitCode;

    /**
     * Creates a process result.
     *
     * @param output Process output.
     * @param exitCode Process exit code.
     */
    public GProcessResult(final List<String> output, final Integer exitCode) {
        this.output = output;
        this.exitCode = exitCode;
    }
}
