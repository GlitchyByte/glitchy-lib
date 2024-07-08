// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.terminal.display;

import com.glitchybyte.glib.concurrent.GTask;

/**
 * Convenience task to set up a terminal UI.
 */
public final class GTerminalDisplayTask extends GTask {

    private final GRootPanel rootPanel;

    /**
     * Creates a terminal display task with the given root panel.
     *
     * @param rootPanel Root panel containing UI.
     */
    public GTerminalDisplayTask(final GRootPanel rootPanel) {
        super("terminal-display");
        this.rootPanel = rootPanel;
    }

    @Override
    public void run() {
        try {
            // Start data task.
            getTaskRunner().start(GRootPanel.getData());
            // Draw 1st imprint.
            rootPanel.refreshAll();
            rootPanel.draw();
            started();
            // Draw loop.
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
