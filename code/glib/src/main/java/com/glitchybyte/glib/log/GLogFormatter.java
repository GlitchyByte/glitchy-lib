// Copyright 2014-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import com.glitchybyte.glib.GStrings;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Base log formatter class.
 *
 * <p>Uses a compact time format and shows thread name.
 * Made to log one-liners (except for exceptions).
 */
public abstract class GLogFormatter extends SimpleFormatter {

    private static final DateTimeFormatter localDateFormatter =
            DateTimeFormatter.ofPattern("uuuu-MM-dd|HH:mm:ss.SSS", Locale.US)
                    .withZone(ZoneOffset.systemDefault());

    /**
     * Creates a base log formatter.
     */
    protected GLogFormatter() {
        // No-op.
    }

    private String getDateTime(final LogRecord record) {
        return localDateFormatter.format(record.getInstant());
    }

    private String getLevel(final LogRecord record) {
        return record.getLevel().getName();
    }

    private String getClassName(final LogRecord record) {
        final String fullClassName = record.getSourceClassName();
        if (fullClassName == null) {
            return "(null)";
        }
        final int p = fullClassName.lastIndexOf('.');
        if (p == -1) {
            return fullClassName;
        }
        return fullClassName.substring(p + 1);
    }

    private String getMessage(final LogRecord record) {
        return formatMessage(record);
    }

    private Throwable getThrowable(final LogRecord record) {
        return record.getThrown();
    }

    @Override
    public String format(final LogRecord record) {
        final String dateTime = getDateTime(record);
        final String level = getLevel(record);
        final String threadName = Thread.currentThread().getName();
        final String className = getClassName(record);
        final String message = getMessage(record);
        final Throwable throwable = getThrowable(record);
        // Build log line.
        final StringBuilder sb = new StringBuilder();
        applyDateTime(sb, dateTime);
        sb.append(' ');
        applyLevel(sb, level);
        sb.append(' ');
        applyThreadName(sb, "[" + threadName + "]");
        sb.append(' ');
        applyClassName(sb, className + ":");
        sb.append(' ');
        applyMessage(sb, message);
        sb.append(GStrings.NEW_LINE);
        if (throwable != null) {
            appendThrowable(sb, throwable);
        }
        return sb.toString();
    }

    private void appendThrowable(final StringBuilder sb, final Throwable throwable) {
        sb.append(GStrings.SPACE_TAB);
        applyThrowableMessage(sb, throwable.toString());
        sb.append(GStrings.NEW_LINE);
        Set<String> traces = appendStackTrace(sb, throwable.getStackTrace(), Collections.emptySet());
        Throwable cause = throwable.getCause();
        while (cause != null) {
            sb.append(GStrings.SPACE_TAB);
            applyThrowableMessage(sb, "Cause: ");
            applyThrowableMessage(sb, cause.toString());
            sb.append(GStrings.NEW_LINE);
            traces = appendStackTrace(sb, cause.getStackTrace(), traces);
            cause = cause.getCause();
        }
    }

    private Set<String> appendStackTrace(final StringBuilder sb, final StackTraceElement[] stackTrace,
            final Set<String> lastTraces) {
        final Set<String> traces = new HashSet<>();
        for (final var trace: stackTrace) {
            final String traceLine = trace.toString();
            sb.append(GStrings.SPACE_TAB);
            applyThrowableTraceLine(sb, traceLine);
            sb.append(GStrings.NEW_LINE);
            traces.add(traceLine);
            if (lastTraces.contains(traceLine)) {
                sb.append(GStrings.SPACE_TAB);
                applyThrowableTraceLine(sb, "... (" + (stackTrace.length - traces.size()) + " more)");
                sb.append(GStrings.NEW_LINE);
                break;
            }
        }
        return traces;
    }

    /**
     * Applies the {@code datetime} value to the log being built
     * by the given {@code StringBuilder}.
     *
     * @param sb Log message builder.
     * @param value Value to apply.
     */
    protected abstract void applyDateTime(final StringBuilder sb, final String value);

    /**
     * Applies the {@code level} value to the log being built
     * by the given {@code StringBuilder}.
     *
     * @param sb Log message builder.
     * @param value Value to apply.
     */
    protected abstract void applyLevel(final StringBuilder sb, final String value);

    /**
     * Applies the {@code threadName} value to the log being built
     * by the given {@code StringBuilder}.
     *
     * @param sb Log message builder.
     * @param value Value to apply.
     */
    protected abstract void applyThreadName(final StringBuilder sb, final String value);

    /**
     * Applies the {@code className} value to the log being built
     * by the given {@code StringBuilder}.
     *
     * @param sb Log message builder.
     * @param value Value to apply.
     */
    protected abstract void applyClassName(final StringBuilder sb, final String value);

    /**
     * Applies the {@code message} value to the log being built
     * by the given {@code StringBuilder}.
     *
     * @param sb Log message builder.
     * @param value Value to apply.
     */
    protected abstract void applyMessage(final StringBuilder sb, final String value);

    /**
     * Applies the {@code throwableMessage} value to the log being built
     * by the given {@code StringBuilder}.
     *
     * @param sb Log message builder.
     * @param value Value to apply.
     */
    protected abstract void applyThrowableMessage(final StringBuilder sb, final String value);

    /**
     * Applies the {@code throwableTraceLine} value to the log being built
     * by the given {@code StringBuilder}.
     *
     * @param sb Log message builder.
     * @param value Value to apply.
     */
    protected abstract void applyThrowableTraceLine(final StringBuilder sb, final String value);
}
