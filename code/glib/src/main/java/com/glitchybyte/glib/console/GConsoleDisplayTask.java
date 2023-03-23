// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.console;

import com.glitchybyte.glib.concurrent.GConcurrentTask;

/**
 * Convenience task to setup a console UI.
 */
public final class GConsoleDisplayTask extends GConcurrentTask {

    private final GRootPanel rootPanel;

    /**
     * Creates a console display task with the given root panel.
     *
     * @param rootPanel Root panel containing UI.
     */
    public GConsoleDisplayTask(final GRootPanel rootPanel) {
        this.rootPanel = rootPanel;
    }

    @Override
    public void run() {
        rootPanel.refresh();
        rootPanel.draw();
        started();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                rootPanel.awaitDirty();
                rootPanel.refreshDirty();
                rootPanel.draw();
            }
        } catch (final InterruptedException e) {
            // Exiting!
        }
    }
}
