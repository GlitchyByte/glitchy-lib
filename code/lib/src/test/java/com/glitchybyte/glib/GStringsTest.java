// Copyright 2021-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GStringsTest {

    @Test
    void singleLineIndent() {
        final String input = "One line.";
        final String output = GStrings.indent(input);
        assertEquals("  One line.", output);
    }

    @Test
    void multipleLineIndent() {
        final String input = "One line." + GStrings.NEW_LINE + "Two lines.";
        final String output = GStrings.indent(input);
        assertEquals(GStrings.SPACE_TAB + "One line." + GStrings.NEW_LINE + GStrings.SPACE_TAB + "Two lines.", output);
    }

    @Test
    void formatFloat() {
        final float value = 1.9876f;
        final String output = GStrings.fromFloat(value);
        assertEquals("1.988", output);
    }

    @Test
    void formatDouble() {
        final double value = 1.9876;
        final String output = GStrings.fromDouble(value);
        assertEquals("1.988", output);
    }

    @Test
    void formatInt() {
        final int value = 53280;
        final String output = GStrings.fromInt(value);
        assertEquals("53,280", output);
    }

    @Test
    void formatLong() {
        final long value = 64738L;
        final String output = GStrings.fromLong(value);
        assertEquals("64,738", output);
    }

    @Test
    void convertCollectionToString() {
        final List<Integer> list = List.of(3, 0, 8);
        final String output = GStrings.fromCollection(list, ":");
        assertEquals("3:0:8", output);
    }

    @Test
    void convertCollectionToStringWithDefaultDelimiter() {
        final List<Integer> list = List.of(3, 0, 8);
        final String output = GStrings.fromCollection(list);
        assertEquals("3, 0, 8", output);
    }

    @Test
    void base64Encode() {
        final String message = "Obfuscate this";
        final String encoded = GStrings.base64Encode(message);
        assertEquals("T2JmdXNjYXRlIHRoaXM=", encoded);
    }

    @Test
    void base64Decode() {
        final String encoded = "T2JmdXNjYXRlIHRoaXM=";
        final String message = GStrings.base64Decode(encoded);
        assertEquals("Obfuscate this", message);
    }

    @Test
    void bytesToGroup() {
        assertEquals("100 b", GStrings.bytesToGroupUnit(100L));
        assertEquals("1 KiB", GStrings.bytesToGroupUnit(1024L));
        assertEquals("1 MiB", GStrings.bytesToGroupUnit(1024L * 1024));
        assertEquals("1 GiB", GStrings.bytesToGroupUnit(1024L * 1024 * 1024));
        assertEquals("1 TiB", GStrings.bytesToGroupUnit(1024L * 1024 * 1024 * 1024));
        assertEquals("1 PiB", GStrings.bytesToGroupUnit(1024L * 1024 * 1024 * 1024 * 1024));
    }

    @Test
    void findIndexOfCharacter() {
        final String input = "Hello world!";
        assertEquals(6, GStrings.indexOfAny(input, Set.of('x', 'w', '!')));
        assertEquals(-1, GStrings.indexOfAny(input, Set.of('x', 'p', '?')));
    }

    @Test
    void findIndexOfCharacterWithOffset() {
        final String input = "Hello world!";
        assertEquals(8, GStrings.indexOfAny(input, Set.of('r', 'w', '!'), 7));
        assertEquals(-1, GStrings.indexOfAny(input, Set.of('x', 'w', '?'), 7));
    }
}
