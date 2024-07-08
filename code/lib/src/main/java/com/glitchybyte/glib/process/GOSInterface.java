// Copyright 2022-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.process;

import com.glitchybyte.glib.GStrings;
import sun.misc.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Singleton instance of an OS interface. The instance will be applicable to the currently running OS.
 *
 * <p>It has been tested on macOS, Linux, and Windows.
 * But Windows needs an external app for proper signal (e.g., SIGINT) sending, if needed.
 * More information in GOSWindows.
 */
public abstract class GOSInterface {

    /**
     * The instance.
     */
    public static final GOSInterface instance = createInstance();

    private static GOSInterface createInstance() {
        final GOSType osType = GOSType.getType(System.getProperty("os.name"));
        return switch (osType) {
            case LINUX, MAC_OS -> new GOSLinux();
            case WINDOWS -> new GOSWindows();
            case UNKNOWN -> new GOSUnknown();
        };
    }

    /**
     * INT signal.
     */
    public static final Signal SIGINT = new Signal("INT");

    /**
     * OS type.
     */
    public final GOSType osType;

    /**
     * Creates OS interface for the given OS type.
     *
     * @param osType OS type from which we want to create an interface.
     */
    protected GOSInterface(final GOSType osType) {
        this.osType = osType;
    }

    /**
     * Determines if the given exit code represents success.
     *
     * @param exitCode Exit code as returned by GProcess.
     * @return True if {@code exitCode} represents success.
     */
    public boolean isSuccessfulExitCode(final Integer exitCode) {
        return (exitCode != null) && (exitCode == 0);
    }

    /**
     * Convenience method to convert a string into an array for proper command array parameter.
     *
     * @param command Command string.
     * @return An array containing the command string.
     * @throws IllegalArgumentException If command has mismatched quotes.
     */
    public String[] makeCommand(final String command) {
        final List<String> parts = new ArrayList<>();
        Set<Character> lookingFor = Set.of(' ', '"', '\'');
        final int cmdLen = command.length();
        int start = 0;
        boolean inQuotes = false;
        while (start < cmdLen) {
            final int p = GStrings.indexOfAny(command, lookingFor, start);
            if (p == -1) {
                if (inQuotes) {
                    throw new IllegalArgumentException("Mismatched quotes!");
                }
                final String part = command.substring(start);
                if (!part.isBlank()) {
                    parts.add(part);
                }
                break;
            } else {
                final char ch = command.charAt(p);
                if (inQuotes) {
                    final String part = command.substring(start, p);
                    inQuotes = false;
                    lookingFor = Set.of(' ', '"', '\'');
                    parts.add(part);
                } else {
                    if ((ch == '"') || (ch == '\'')) {
                        lookingFor = Set.of(ch);
                        inQuotes = true;
                        start = p + 1;
                        continue;
                    } else {
                        final String part = command.substring(start, p);
                        if (!part.isBlank()) {
                            parts.add(part);
                        }
                    }
                }
            }
            start = p + 1;
        }
        return parts.toArray(new String[0]);
    }

    /**
     * Returns a command array ready to call the shell with the given script.
     *
     * @param command Shell command with args.
     *
     * @return A new command string.
     */
    public abstract String[] getShellCommand(final String command);

    /**
     * Creates a process builder.
     *
     * @param command Command string.
     * @param dir Starting directory. If null, it will inherit the current process current directory.
     * @return A process builder.
     */
    public ProcessBuilder createProcessBuilder(final String[] command, final Path dir) {
        final ProcessBuilder pb = new ProcessBuilder();
        if (dir != null) {
            pb.directory(dir.toFile());
        }
        pb.command(command);
        return pb;
    }

    /**
     * Spawns and executes the given process, captures output into the given list, and waits until it is done.
     *
     * <p>This is useful for immediate short-lived commands.
     *
     * @param pb Process builder containing the command.
     * @param output Output container. If null, output will not be captured.
     * @return Process exit code.
     */
    public Integer execute(final ProcessBuilder pb, final List<String> output) {
        try {
            final Process process = pb.start();
            try (final BufferedReader reader = process.inputReader(StandardCharsets.UTF_8)) {
                String line;
                do {
                    line = reader.readLine();
                    if ((output != null) && (line != null)) {
                        output.add(line);
                    }
                } while (line != null);
            }
            return process.waitFor();
        } catch (final IOException | InterruptedException e) {
            return null;
        }
    }

    /**
     * Spawns and executes the given command, captures output into the given list, and waits until it is done.
     *
     * @param command Command string.
     * @param dir Starting directory. If null, it will inherit the current process current directory.
     * @param output Output container. If null, output will not be captured.
     * @return Process exit code.
     */
    public Integer execute(final String[] command, final Path dir, final List<String> output) {
        final ProcessBuilder pb = createProcessBuilder(command, dir);
        return execute(pb, output);
    }

    /**
     * Spawns and executes the given command and waits until it is done.
     *
     * @param command Command string.
     * @param dir Starting directory. If null, it will inherit the current process current directory.
     * @return Process exit code.
     */
    public Integer execute(final String[] command, final Path dir) {
        return execute(command, dir, null);
    }

    /**
     * Spawns and executes the given command and waits until it is done.
     *
     * @param command Command string.
     * @return Process exit code.
     */
    public Integer execute(final String[] command) {
        return execute(command, null);
    }

    /**
     * Spawns and executes the given command and waits until it is done.
     *
     * @param command Command string.
     * @param dir Starting directory. If null, it will inherit the current process current directory.
     * @return A process result object with output and exit code.
     */
    public GProcessResult executeWithResult(final String[] command, final Path dir) {
        final List<String> output = new ArrayList<>();
        final Integer exitCode = execute(command, dir, output);
        return new GProcessResult(output, exitCode);
    }

    /**
     * Spawns and executes the given command and waits until it is done.
     *
     * @param command Command string.
     * @return A process result object with output and exit code.
     */
    public GProcessResult executeWithResult(final String[] command) {
        return executeWithResult(command, null);
    }

    /**
     * Returns the command array that will send the given signal to the pid.
     *
     * @param signal Signal to send.
     * @param pid Pid of process.
     * @return Command array.
     */
    public abstract String[] getSignalCommand(final Signal signal, final long pid);

    /**
     * Sends signal to the given pid.
     *
     * @param signal Signal to send.
     * @param pid Pid of process.
     * @return Process exit code.
     */
    public Integer sendSignal(final Signal signal, final long pid) {
        final String[] command = getSignalCommand(signal, pid);
        return execute(command);
    }

    /**
     * Sends SIGINT to the given pid.
     *
     * @param pid Pid of process.
     * @return Process exit code.
     */
    public Integer sendSignalINT(final long pid) {
        return sendSignal(SIGINT, pid);
    }
}
