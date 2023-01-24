// Copyright 2014-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import java.util.logging.*;

/**
 * Logger class.
 *
 * <p>Made to log one-liners (except for exceptions).
 * Set logger name before use to change default name.
 * Default logging level is CONFIG.
 */
public final class GLog {

    private static String LOGGER_NAME = "com.glitchybyte";

    private static Logger LOGGER = null;

    /**
     * Sets up the root console logger with our custom formatter.
     *
     * @param useColor True for console color output.
     */
    public static void setupRootConsoleLogger(final boolean useColor) {
        final Logger logger = Logger.getLogger("");
        final Formatter formatter = useColor ? new GLogColorFormatter() : new GLogStandardFormatter();
        Handler consoleHandler = null;
        for (final Handler handler: logger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                consoleHandler = handler;
                break;
            }
        }
        if (consoleHandler == null) {
            consoleHandler = new ConsoleHandler();
            logger.addHandler(consoleHandler);
        }
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(Level.ALL);
    }

    private static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = Logger.getLogger(LOGGER_NAME);
            LOGGER.setLevel(Level.ALL);
        }
        return LOGGER;
    }

    /**
     * Resets the default logger.
     *
     * <p>Clears the default logger. Next time a log is issued, a new logger is created.
     */
    public static void resetLogger() {
        LOGGER = null;
    }

    /**
     * Sets the name of the logger for the whole application.
     *
     * @param name Logger name.
     */
    public static void setName(final String name) {
        if (LOGGER != null) {
            throw new IllegalStateException("Logger already created. Name can't be set.");
        }
        LOGGER_NAME = name;
    }

    /**
     * Sets the minimum level to log.
     *
     * @param level Minimum log level.
     */
    public static void setLevel(final Level level) {
        final Logger logger = getLogger();
        logger.setLevel(level);
    }

    /**
     * Logs a message at the specified level.
     *
     * @param level Log level for the message.
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public static void log(final Level level, final String msg, final Object... params) {
        final Logger logger = getLogger();
        final Thread currentThread = Thread.currentThread();
        final StackTraceElement stackTraceElement = currentThread.getStackTrace()[3];
        final String className = stackTraceElement.getClassName();
        final String methodName = stackTraceElement.getMethodName();
        logger.logp(level, className, methodName, msg, params);
    }

    /**
     * Logs a throwable at the specified level.
     *
     * @param level Log level for the message.
     * @param throwable Throwable to log.
     */
    public static void log(final Level level, final Throwable throwable) {
        final Logger logger = getLogger();
        final Thread currentThread = Thread.currentThread();
        final StackTraceElement stackTraceElement = currentThread.getStackTrace()[3];
        final String className = stackTraceElement.getClassName();
        final String methodName = stackTraceElement.getMethodName();
        logger.logp(level, className, methodName, throwable.getMessage(), throwable);
    }

    /**
     * Logs a message at {@code FINEST} level.
     *
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public static void finest(final String msg, final Object... params) {
        log(Level.FINEST, msg, params);
    }

    /**
     * Logs a Throwable at {@code FINEST} level.
     *
     * @param throwable Throwable to log.
     */
    public static void finest(final Throwable throwable) {
        log(Level.FINEST, throwable);
    }

    /**
     * Logs a message at {@code FINER} level.
     *
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public static void finer(final String msg, final Object... params) {
        log(Level.FINER, msg, params);
    }

    /**
     * Logs a Throwable at {@code FINER} level.
     *
     * @param throwable Throwable to log.
     */
    public static void finer(final Throwable throwable) {
        log(Level.FINER, throwable);
    }

    /**
     * Logs a message at {@code FINE} level.
     *
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public static void fine(final String msg, final Object... params) {
        log(Level.FINE, msg, params);
    }

    /**
     * Logs a Throwable at {@code FINE} level.
     *
     * @param throwable Throwable to log.
     */
    public static void fine(final Throwable throwable) {
        log(Level.FINE, throwable);
    }

    /**
     * Logs a message at {@code CONFIG} level.
     *
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public static void config(final String msg, final Object... params) {
        log(Level.CONFIG, msg, params);
    }

    /**
     * Logs a Throwable at {@code CONFIG} level.
     *
     * @param throwable Throwable to log.
     */
    public static void config(final Throwable throwable) {
        log(Level.CONFIG, throwable);
    }

    /**
     * Logs a message at {@code INFO} level.
     *
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public static void info(final String msg, final Object... params) {
        log(Level.INFO, msg, params);
    }

    /**
     * Logs a Throwable at {@code INFO} level.
     *
     * @param throwable Throwable to log.
     */
    public static void info(final Throwable throwable) {
        log(Level.INFO, throwable);
    }

    /**
     * Logs a message at {@code WARNING} level.
     *
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public static void warning(final String msg, final Object... params) {
        log(Level.WARNING, msg, params);
    }

    /**
     * Logs a Throwable at {@code WARNING} level.
     *
     * @param throwable Throwable to log.
     */
    public static void warning(final Throwable throwable) {
        log(Level.WARNING, throwable);
    }

    /**
     * Logs a message at {@code SEVERE} level.
     *
     * @param msg Message to log.
     * @param params Message parameters.
     */
    public static void severe(final String msg, final Object... params) {
        log(Level.SEVERE, msg, params);
    }

    /**
     * Logs a Throwable at {@code SEVERE} level.
     *
     * @param throwable Throwable to log.
     */
    public static void severe(final Throwable throwable) {
        log(Level.SEVERE, throwable);
    }

    /**
     * Logs tests for all possible levels plus an exception.
     */
    public static void logTest() {
        severe("severe");
        warning("warning");
        info("info");
        config("config");
        fine("fine");
        finer("finer");
        finest("finest");
        severe(new IllegalStateException("Exception!"));
    }

    private GLog() {
        // Hiding constructor.
    }
}
