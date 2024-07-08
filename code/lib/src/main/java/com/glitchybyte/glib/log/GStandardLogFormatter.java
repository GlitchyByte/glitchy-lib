// Copyright 2014-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

/**
 * Standard log formatter class.
 *
 * <p>Uses a compact time format and shows thread name.
 * Made to log one-liners (except for exceptions).
 */
public class GStandardLogFormatter extends GLogFormatter {

    /**
     * Creates a standard log formatter.
     */
    public GStandardLogFormatter() {
        // No-op.
    }

    @Override
    protected void applyDateTime(final StringBuilder sb, final String value) {
        sb.append(value);
    }

    @Override
    protected void applyLevel(final StringBuilder sb, final String value) {
        sb.append(value);
    }

    @Override
    protected void applyThreadName(final StringBuilder sb, final String value) {
        sb.append(value);
    }

    @Override
    protected void applyClassName(final StringBuilder sb, final String value) {
        sb.append(value);
    }

    @Override
    protected void applyMessage(final StringBuilder sb, final String value) {
        sb.append(value);
    }

    @Override
    protected void applyThrowableMessage(final StringBuilder sb, final String value) {
        sb.append(value);
    }

    @Override
    protected void applyThrowableTraceLine(final StringBuilder sb, final String value) {
        sb.append(value);
    }
}
