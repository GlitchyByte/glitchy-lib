// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.console.display;

import com.glitchybyte.glib.GObjects;
import com.glitchybyte.glib.concurrent.GConcurrentTask;
import com.glitchybyte.glib.concurrent.GLock;
import com.glitchybyte.glib.concurrent.workqueue.GWorkQueue;
import com.glitchybyte.glib.concurrent.workqueue.GWorkQueueTask;
import com.glitchybyte.glib.log.GLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Task to handle data for panels in a generic way.
 */
public final class GDisplayDataTask extends GConcurrentTask {

    private static final class PanelData {

        public final GPanel panel;
        public volatile Object data = null;

        public PanelData(final GPanel panel) {
            this.panel = panel;
        }
    }

    private static final class DataChange {

        public final String key;
        public final Object data;

        public DataChange(final String key, final Object data) {
            this.key = key;
            this.data = data;
        }
    }

    private final GWorkQueueTask<DataChange> panelDataChanges;
    private final Map<String, PanelData> panels = new HashMap<>();
    private final ReadWriteLock panelsLock = new ReentrantReadWriteLock();

    /**
     * Creates a display data object.
     */
    public GDisplayDataTask() {
        super("console-data");
        panelDataChanges = new GWorkQueue.Builder<DataChange>()
                .withProcessor(this::processDataChange)
                .build();
    }

    @Override
    public void run() {
        try {
            getTaskRunner().start(panelDataChanges);
            started();
        } catch (final InterruptedException e) {
            // This is early in the application lifetime, so we just explode.
            throw new IllegalStateException("Display data couldn't start!");
        }
    }

    private void processDataChange(final DataChange dataChange) {
        final PanelData panelData;
        panelsLock.writeLock().lock();
        try {
            panelData = panels.get(dataChange.key);
            if (panelData == null) {
                GLog.warning("No panel associated with key: {0}", dataChange.key);
                return;
            }
            panelData.data = dataChange.data;
        } finally {
            panelsLock.writeLock().unlock();
        }
        panelData.panel.signalDirty();
    }

    /**
     * Registers a key to a panel.
     *
     * @param key Key for data.
     * @param panel Panel that will be refreshed when data is put on that key.
     */
    public void registerPanel(final String key, final GPanel panel) {
        GLock.writeLocked(panelsLock, () -> panels.put(key, new PanelData(panel)));
    }

    /**
     * Puts data on key. Triggering the registered panel to refresh.
     *
     * @param key Key for data.
     * @param data Data.
     */
    public void put(final String key, final Object data) {
        panelDataChanges.addWork(new DataChange(key, data));
    }

    /**
     * Returns data as the given type.
     *
     * @param key Key for data.
     * @param tClass Class for data.
     * @return Data as the given type.
     * @param <T> Type of data.
     */
    public <T> T getAs(final String key, final Class<T> tClass) {
        panelsLock.readLock().lock();
        try {
            final PanelData panelData = panels.get(key);
            return GObjects.castOrNull(panelData.data, tClass);
        } finally {
            panelsLock.readLock().unlock();
        }
    }

    /**
     * Returns data as int.
     *
     * @param key Key for data.
     * @return Data as int.
     */
    public int getAsInt(final String key) {
        return getAs(key, Integer.class);
    }

    /**
     * Returns data as long.
     *
     * @param key Key for data.
     * @return Data as long.
     */
    public long getAsLong(final String key) {
        return getAs(key, Long.class);
    }

    /**
     * Returns data as float.
     *
     * @param key Key for data.
     * @return Data as float.
     */
    public float getAsFloat(final String key) {
        return getAs(key, Float.class);
    }

    /**
     * Returns data as double.
     *
     * @param key Key for data.
     * @return Data as double.
     */
    public double getAsDouble(final String key) {
        return getAs(key, Double.class);
    }

    /**
     * Returns data as String.
     *
     * @param key Key for data.
     * @return Data as String.
     */
    public String getAsString(final String key) {
        return getAs(key, String.class);
    }
}
