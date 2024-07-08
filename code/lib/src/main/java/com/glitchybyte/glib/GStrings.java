// Copyright 2014-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Base64;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * String utilities.
 */
public final class GStrings {

    /**
     * System new line separator.
     */
    public static final String NEW_LINE = System.lineSeparator();

    /**
     * Spaces for indentation.
     */
    public static final String SPACE_TAB = "  ";

    /**
     * Number format for minimal human representation of double as String.
     */
    private static final Format numberFormat = DecimalFormat.getNumberInstance(Locale.US);

    /**
     * Convenience for common test of a null or empty string.
     *
     * @param str String to test.
     * @return True, if the string is null or empty.
     */
    public static boolean isNullOrEmpty(final String str) {
        return (str == null) || str.isEmpty();
    }

    /**
     * Indents the string. It will correctly indent multiple lines
     * as long as they have system line separators.
     *
     * <p>Unlike {@code String.indent}, this function does not add a new line if it doesn't exist.
     *
     * @param str String to indent.
     * @return Indented string.
     */
    public static String indent(final String str) {
        return str.contains(NEW_LINE) ?
                SPACE_TAB + str.replaceAll(NEW_LINE, NEW_LINE + SPACE_TAB) :
                SPACE_TAB + str;
    }

    /**
     * Convenience replacement for {@code String.format} that already includes US locale.
     *
     * @param format A format string.
     * @param args Arguments for format.
     * @return Formatted string.
     */
    public static String format(final String format, final Object... args) {
        return String.format(Locale.US, format, args);
    }

    /**
     * Convenience replacement for {@code String.toLowerCase} that already includes US locale.
     *
     * @param str String to convert.
     * @return Converted string.
     */
    public static String toLowerCase(final String str) {
        return str == null ? null : str.toLowerCase(Locale.US);
    }

    /**
     * Convenience replacement for {@code String.toUpperCase} that already includes US locale.
     *
     * @param str String to convert.
     * @return Converted string.
     */
    public static String toUpperCase(final String str) {
        return str == null ? null : str.toUpperCase(Locale.US);
    }

    /**
     * Convenience double to String formatter with human minimal representation.
     *
     * @param value Double to format into a String.
     * @return Minimal String representation of the value.
     */
    public static String fromDouble(final double value) {
        return numberFormat.format(value);
    }

    /**
     * Convenience float to String formatter with human minimal representation.
     *
     * @param value Float to format into a String.
     * @return Minimal String representation of the value.
     */
    public static String fromFloat(final float value) {
        return numberFormat.format(value);
    }

    /**
     * Convenience int to String formatter with human minimal representation.
     *
     * @param value Int to format into a String.
     * @return Minimal String representation of the value.
     */
    public static String fromInt(final int value) {
        return format("%,d", value);
    }

    /**
     * Convenience long to String formatter with human minimal representation.
     *
     * @param value Long to format into a String.
     * @return Minimal String representation of the value.
     */
    public static String fromLong(final long value) {
        return format("%,d", value);
    }

    /**
     * Convenience Collection to String representation.
     *
     * @param collection Collection to represent as String.
     * @param delimiter Delimiter to use between elements.
     * @return A String representing the collection.
     * @param <T> Type of elements in the collection.
     */
    public static <T> String fromCollection(final Collection<T> collection, final String delimiter) {
        return collection.stream().map(T::toString).collect(Collectors.joining(delimiter));
    }

    /**
     * Convenience Collection to String representation with default comma delimiter.
     *
     * @param collection Collection to represent as String.
     * @return A String representing the collection.
     * @param <T> Type of elements in the collection.
     */
    public static <T> String fromCollection(final Collection<T> collection) {
        return fromCollection(collection, ", ");
    }

    /**
     * Convenience base64 encoder. Assumes UTF-8 charset.
     *
     * @param message Message to encode.
     * @return Encoded message.
     */
    public static String base64Encode(final String message) {
        return Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Convenience base64 decoder. Assumes UTF-8 charset.
     *
     * @param encoded Encoded message.
     * @return Decoded message.
     */
    public static String base64Decode(final String encoded) {
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    /**
     * Converts a number of bytes to its most reasonable grouping representation.
     *
     * @param bytes Quantity of bytes.
     * @return A string representing those bytes in human-readable form.
     */
    public static String bytesToGroupUnit(final long bytes) {
        double quantity = bytes;
        String denomination = "b";
        if (quantity >= 1000) {
            quantity /= 1024;
            denomination = "KiB";
        }
        if (quantity >= 1000) {
            quantity /= 1024;
            denomination = "MiB";
        }
        if (quantity >= 1000) {
            quantity /= 1024;
            denomination = "GiB";
        }
        if (quantity >= 1000) {
            quantity /= 1024;
            denomination = "TiB";
        }
        if (quantity >= 1000) {
            quantity /= 1024;
            denomination = "PiB";
        }
        return GStrings.format("%s %s", GStrings.fromDouble(quantity), denomination);
    }

    /**
     * Returns the index of the 1st character in the set found in the string.
     *
     * @param str Input string.
     * @param characterSet Characters to find.
     * @param offset Offset from the beginning of the string.
     * @return The index of the 1st character in the set found in the string, or -1 of not found.
     */
    public static int indexOfAny(final String str, final Set<Character> characterSet, final int offset) {
        int found = -1;
        for (final char ch: characterSet) {
            final int p = str.indexOf(ch, offset);
            if (p == -1) {
                continue;
            }
            if ((found == -1) || (p < found)) {
                found = p;
            }
        }
        return found;
    }

    /**
     * Returns the index of the 1st character in the set found in the string.
     *
     * @param str Input string.
     * @param characterSet Characters to find.
     * @return The index of the 1st character in the set found in the string, or -1 of not found.
     */
    public static int indexOfAny(final String str, final Set<Character> characterSet) {
        return indexOfAny(str, characterSet, 0);
    }

    private GStrings() {
        // Hiding constructor.
    }
}
