// Copyright 2015-2024 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.json;

import com.glitchybyte.glib.log.GLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A wrapper around a Gson object that extends its capabilities.
 * <p>
 * Just like Gson, a GJson object is thread-safe.
 */
public final class GJson {

    private static final class DefaultInstanceHolder {
        private static final GJson instance = new GJson(new Gson());
    }

    /**
     * Returns a singleton default basic {@code GJson}.
     *
     * @return A singleton default basic {@code GJson}.
     */
    public static GJson defaultInstance() {
        return DefaultInstanceHolder.instance;
    }

    private static final class PrettyInstanceHolder {
        private static final GJson instance = new GJson(new GsonBuilder().setPrettyPrinting().create());
    }

    /**
     * Returns a singleton {@code GJson} with pretty printing.
     *
     * @return A singleton {@code GJson} with pretty printing.
     */
    public static GJson prettyInstance() {
        return PrettyInstanceHolder.instance;
    }

    private final Gson gson;

    /**
     * Creates a json helper wrapper around a {@code Gson} object.
     *
     * @param gson Actual Gson object doing json work.
     */
    public GJson(final Gson gson) {
        this.gson = gson;
    }

    /**
     * Create object from {@code String}.
     *
     * @param json Json string.
     * @param tClass Class of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromString(final String json, final Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    /**
     * Create object from {@code String}.
     *
     * @param json Json string.
     * @param type Type of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromString(final String json, final Type type) {
        return gson.fromJson(json, type);
    }

    /**
     * Create object from {@code String} representation of a path.
     *
     * @param path String representation of the path to read json from.
     * @param tClass Class of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromPath(final String path, final Class<T> tClass) {
        return fromPath(Paths.get(path), tClass);
    }

    /**
     * Create object from {@code String} representation of a path.
     *
     * @param path String representation of the path to read json from.
     * @param type Type of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromPath(final String path, final Type type) {
        return fromPath(Paths.get(path), type);
    }

    /**
     * Create object from {@code File}.
     *
     * @param file File to read json from.
     * @param tClass Class of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromFile(final File file, final Class<T> tClass) {
        return fromPath(file.toPath(), tClass);
    }

    /**
     * Create object from {@code File}.
     *
     * @param file File to read json from.
     * @param type Type of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromFile(final File file, final Type type) {
        return fromPath(file.toPath(), type);
    }

    /**
     * Create object from {@code Path}.
     *
     * @param path Path to read json from.
     * @param tClass Class of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromPath(final Path path, final Class<T> tClass) {
        try (final Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return fromReader(reader, tClass);
        } catch (final IOException e) {
            GLog.warning(e);
        }
        return null;
    }

    /**
     * Create object from {@code Path}.
     *
     * @param path Path to read json from.
     * @param type Type of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromPath(final Path path, final Type type) {
        try (final Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return fromReader(reader, type);
        } catch (final IOException e) {
            GLog.warning(e);
        }
        return null;
    }

    /**
     * Create object from {@code InputStream}.
     *
     * @param stream InputStream to read json from.
     * @param tClass Class of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromInputStream(final InputStream stream, final Class<T> tClass) {
        final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        try (final BufferedReader bufferedReader = new BufferedReader(reader)) {
            return fromReader(bufferedReader, tClass);
        } catch (final IOException e) {
            GLog.warning(e);
        }
        return null;
    }

    /**
     * Create object from {@code InputStream}.
     *
     * @param stream InputStream to read json from.
     * @param type Type of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromInputStream(final InputStream stream, final Type type) {
        final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        try (final BufferedReader bufferedReader = new BufferedReader(reader)) {
            return fromReader(bufferedReader, type);
        } catch (final IOException e) {
            GLog.warning(e);
        }
        return null;
    }

    /**
     * Create object from {@code Reader}.
     *
     * @param reader Reader producing json.
     * @param tClass Class of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromReader(final Reader reader, final Class<T> tClass) {
        return gson.fromJson(reader, tClass);
    }

    /**
     * Create object from {@code Reader}.
     *
     * @param reader Reader producing json.
     * @param type Type of resulting object.
     * @param <T> Type of desired object.
     * @return Object populated from json.
     */
    public <T> T fromReader(final Reader reader, final Type type) {
        return gson.fromJson(reader, type);
    }

    /**
     * Return object as json {@code String}.
     *
     * @param object Object to convert to json.
     * @return Json string.
     */
    public String toString(final Object object) {
        return gson.toJson(object);
    }

    /**
     * Write json object to path represented by a {@code String}.
     *
     * @param object Object to convert to json and write.
     * @param path String representation of the path to write json to.
     */
    public void toPath(final Object object, final String path) {
        toPath(object, Path.of(path));
    }

    /**
     * Write json object to {@code File}.
     *
     * @param object Object to convert to json and write.
     * @param file File to write json to.
     */
    public void toFile(final Object object, final File file) {
        toPath(object, file.toPath());
    }

    /**
     * Write json object to {@code Path}.
     *
     * @param object Object to convert to json and write.
     * @param path Path to write json to.
     */
    public void toPath(final Object object, final Path path) {
        try (final Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            toWriter(object, writer);
        } catch (final IOException e) {
            GLog.warning(e);
        }
    }

    /**
     * Write json object to {@code Writer}.
     *
     * @param object Object to convert to json and write.
     * @param writer Writer to write json to.
     */
    public void toWriter(final Object object, final Writer writer) {
        gson.toJson(object, writer);
    }
}
