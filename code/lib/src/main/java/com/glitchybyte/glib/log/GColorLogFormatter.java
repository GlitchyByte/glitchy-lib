// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import com.glitchybyte.glib.terminal.GTerminal;

/**
 * Color log formatter class.
 *
 * <p>Uses a compact time format and shows thread name.
 * Made to log one-liners (except for exceptions).
 */
public class GColorLogFormatter extends GLogFormatter {

    private static final String COLOR_DATETIME = GTerminal.foregroundColor(GTerminal.rgb(1, 1, 1));
    private static final String COLOR_LEVEL_SEVERE = GTerminal.foregroundColor(GTerminal.rgb(5, 2, 2));
    private static final String COLOR_LEVEL_WARNING = GTerminal.foregroundColor(GTerminal.rgb(5, 4, 2));
    private static final String COLOR_LEVEL_INFO = GTerminal.foregroundColor(GTerminal.rgb(2, 5, 2));
    private static final String COLOR_LEVEL_CONFIG = GTerminal.foregroundColor(GTerminal.rgb(2, 4, 5));
    private static final String COLOR_LEVEL_FINE = GTerminal.foregroundColor(GTerminal.rgb(3, 3, 5));
    private static final String COLOR_LEVEL_FINER = GTerminal.foregroundColor(GTerminal.rgb(2, 2, 5));
    private static final String COLOR_LEVEL_FINEST = GTerminal.foregroundColor(GTerminal.rgb(1, 1, 5));
    private static final String COLOR_THREAD_NAME = GTerminal.foregroundColor(GTerminal.rgb(1, 1, 1));
    private static final String COLOR_CLASS_NAME = GTerminal.foregroundColor(GTerminal.rgb(5, 5, 5));
    private static final String COLOR_THROWABLE_MESSAGE = GTerminal.foregroundColor(GTerminal.rgb(5, 4, 4));
    private static final String COLOR_THROWABLE_TRACE_LINE = GTerminal.foregroundColor(GTerminal.rgb(5, 1, 1));

    /**
     * Creates a color log formatter.
     */
    public GColorLogFormatter() {
        // No-op.
    }

    @Override
    protected void applyDateTime(final StringBuilder sb, final String value) {
        sb.append(COLOR_DATETIME);
        sb.append(value);
        sb.append(GTerminal.resetColor());
    }

    @Override
    protected void applyLevel(final StringBuilder sb, final String value) {
        sb.append(switch (value) {
            case "SEVERE" -> COLOR_LEVEL_SEVERE;
            case "WARNING" -> COLOR_LEVEL_WARNING;
            case "INFO" -> COLOR_LEVEL_INFO;
            case "CONFIG" -> COLOR_LEVEL_CONFIG;
            case "FINE" -> COLOR_LEVEL_FINE;
            case "FINER" -> COLOR_LEVEL_FINER;
            case "FINEST" -> COLOR_LEVEL_FINEST;
            default -> GTerminal.COLOR_WHITE;
        });
        sb.append(value);
        sb.append(GTerminal.resetColor());
    }

    @Override
    protected void applyThreadName(final StringBuilder sb, final String value) {
        sb.append(COLOR_THREAD_NAME);
        sb.append(value);
        sb.append(GTerminal.resetColor());
    }

    @Override
    protected void applyClassName(final StringBuilder sb, final String value) {
        sb.append(COLOR_CLASS_NAME);
        sb.append(value);
        sb.append(GTerminal.resetColor());
    }

    @Override
    protected void applyMessage(final StringBuilder sb, final String value) {
        sb.append(value);
    }

    @Override
    protected void applyThrowableMessage(final StringBuilder sb, final String value) {
        sb.append(COLOR_THROWABLE_MESSAGE);
        sb.append(value);
        sb.append(GTerminal.resetColor());
    }

    @Override
    protected void applyThrowableTraceLine(final StringBuilder sb, final String value) {
        sb.append(COLOR_THROWABLE_TRACE_LINE);
        sb.append(value);
        sb.append(GTerminal.resetColor());
    }
}
