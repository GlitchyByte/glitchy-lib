// Copyright 2014-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.log;

import com.glitchybyte.glib.GStrings;

import java.io.*;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.*;

/**
 * Logger class.
 *
 * <p>Set logger name before use to change default name.
 * Default logging level is {@code ALL}.
 *
 * <p>To properly use this facility it must be setup with one of the following
 * methods before any logging happens:
 * <ul>
 *     <li>{@code setupRootHandler}
 *     <li>{@code setupDefaultRootConsoleHandler}
 *     {@code <-} if you aren't sure, this one is probably what you want.
 *     <li>{@code setupRootLineCollectorHandler}
 *     <li>{@code setupDefaultRootLineCollectorHandler}
 * </ul>
 */
public final class GLog {

    private static String LOGGER_NAME = "com.glitchybyte";

    private static Logger LOGGER = null;

    /**
     * Sets up the root handler to the given handler.
     *
     * <p>The default {@code ConsoleHandler} will be removed and replaced with
     * the given one.
     *
     * @param handler The desired root handler.
     */
    public static void setupRootHandler(final Handler handler) {
        // Get root logger.
        final Logger logger = Logger.getLogger("");
        // Remove default console logger.
        Arrays.stream(logger.getHandlers())
                .filter(ConsoleHandler.class::isInstance)
                .findFirst()
                .ifPresent(logger::removeHandler);
        // Add our logger. Let's hope it is a console logger, but it's not necessary.
        logger.addHandler(handler);
    }

    /**
     * Sets up the root handler to a custom console handler with custom formatting.
     *
     * @param useColor True for console color output.
     */
    public static void setupDefaultRootConsoleHandler(final boolean useColor) {
        final ConsoleHandler consoleHandler = useColor ? new GColorConsoleHandler() : new GStandardConsoleHandler();
        setupRootHandler(consoleHandler);
    }

    /**
     * Sets up the root handler to a custom handler that collects lines in a
     * buffer. The handler will use the given formatter.
     *
     * @param formatter Log formatter.
     * @return A {@code GLineCollector} that collects logs.
     */
    public static GLineCollector setupRootLineCollectorHandler(final Formatter formatter) {
        final GLineCollector lineCollector;
        final PipedOutputStream outputStream = new PipedOutputStream();
        try {
            final InputStream inputStream = new PipedInputStream(outputStream);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            lineCollector = new GLineCollector(reader).start();
        } catch (final IOException e) {
            throw new IllegalStateException("Error setting up line collector handler!", e);
        }
        GLog.setupRootHandler(new GStringStreamHandler(outputStream, formatter));
        return lineCollector;
    }

    /**
     * Sets up the root handler to a custom handler that collects lines in a
     * buffer. The handler will use a custom formatter that can be made to use
     * colors or not.
     *
     * @param useColor True for console color output.
     * @return A {@code GLineCollector} that collects logs.
     */
    public static GLineCollector setupDefaultRootLineCollectorHandler(final boolean useColor) {
        final Formatter formatter = useColor ? new GColorLogFormatter() : new GStandardLogFormatter();
        return setupRootLineCollectorHandler(formatter);
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
     * @param level Log level for message.
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
     * Logs a message at the specified level.
     *
     * @param level Log level for message.
     * @param supplier Supplier of message.
     */
    public static void log(final Level level, final Supplier<String> supplier) {
        final Logger logger = getLogger();
        final Thread currentThread = Thread.currentThread();
        final StackTraceElement stackTraceElement = currentThread.getStackTrace()[3];
        final String className = stackTraceElement.getClassName();
        final String methodName = stackTraceElement.getMethodName();
        logger.logp(level, className, methodName, supplier);
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
     * Logs a message at {@code FINEST} level.
     *
     * @param supplier Supplier of message.
     */
    public static void finest(final Supplier<String> supplier) {
        log(Level.FINEST, supplier);
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
     * Logs a message at {@code FINER} level.
     *
     * @param supplier Supplier of message.
     */
    public static void finer(final Supplier<String> supplier) {
        log(Level.FINER, supplier);
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
     * Logs a message at {@code FINE} level.
     *
     * @param supplier Supplier of message.
     */
    public static void fine(final Supplier<String> supplier) {
        log(Level.FINE, supplier);
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
     * Logs a message at {@code CONFIG} level.
     *
     * @param supplier Supplier of message.
     */
    public static void config(final Supplier<String> supplier) {
        log(Level.CONFIG, supplier);
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
     * Logs a message at {@code INFO} level.
     *
     * @param supplier Supplier of message.
     */
    public static void info(final Supplier<String> supplier) {
        log(Level.INFO, supplier);
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
     * Logs a message at {@code WARNING} level.
     *
     * @param supplier Supplier of message.
     */
    public static void warning(final Supplier<String> supplier) {
        log(Level.WARNING, supplier);
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
     * Logs a message at {@code SEVERE} level.
     *
     * @param supplier Supplier of message.
     */
    public static void severe(final Supplier<String> supplier) {
        log(Level.SEVERE, supplier);
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

    /**
     * Logs environment stats at {@code CONFIG} level.
     */
    public static void logEnvironmentStats() {
        final Runtime runtime = Runtime.getRuntime();
        final long maxMemory = runtime.maxMemory();
        final long totalMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        config("""
                Environment Stats:
                         Cores: {0}
                       Max mem: {1}
                 Allocated mem: {2}
                      Free mem: {3}
                Total free mem: {4}
                """,
                runtime.availableProcessors(),
                GStrings.bytesToGroupUnit(maxMemory),
                GStrings.bytesToGroupUnit(totalMemory),
                GStrings.bytesToGroupUnit(freeMemory),
                GStrings.bytesToGroupUnit(freeMemory + maxMemory - totalMemory)
        );
    }

    private GLog() {
        // Hiding constructor.
    }
}
