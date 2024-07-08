// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.terminal.display;

import com.glitchybyte.glib.GStrings;
import com.glitchybyte.glib.concurrent.workqueue.GAsyncWorkQueue;
import com.glitchybyte.glib.terminal.GTerminal;

/**
 * Terminal UI root panel that contains all other panels.
 */
public abstract class GRootPanel extends GPanel {

    private static final GDisplayDataTask DATA = new GDisplayDataTask();

    /**
     * Returns an object to read and write data for the display.
     *
     * @return An object to read and write data for the display.
     */
    public static GDisplayDataTask getData() {
        return DATA;
    }

    private final GAsyncWorkQueue<GPanel> dirtyPanels = new GAsyncWorkQueue<>();

    /**
     * Creates a root panel.
     *
     * @param width Panel width.
     * @param height Panel height.
     */
    public GRootPanel(final int width, final int height) {
        super(0, 0, 0, width, height);
        rootPanel = this;
        // Prepare drawing area.
        GTerminal.print(GStrings.NEW_LINE.repeat(height));
        GTerminal.flush();
    }

    /**
     * Awaits for a panel to be dirty.
     *
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    public void awaitDirty() throws InterruptedException {
        dirtyPanels.awaitWork();
    }

    /**
     * Refreshes imprints for all dirty panels.
     */
    public void refreshDirty() {
        dirtyPanels.doWork(GPanel::refresh);
    }

    /**
     * Signals the given panel's contents have changed and needs to be refreshed.
     *
     * @param panel Panel that needs to be refreshed.
     */
    public void signalDirty(final GPanel panel) {
        dirtyPanels.addWork(panel);
    }

    /**
     * Draws the whole UI, all panels.
     */
    public void draw() {
        GTerminal.print(GTerminal.cursorUp(height));
        drawPanel();
        GTerminal.print(GTerminal.cursorDown(height));
        GTerminal.print(GTerminal.CC_CR);
        GTerminal.print(GTerminal.resetColor());
        GTerminal.flush();
    }
}
