// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.console;

import com.glitchybyte.glib.GStrings;
import com.glitchybyte.glib.concurrent.GAsyncWorkQueue;

public abstract class GRootPanel extends GPanel {

    private final GAsyncWorkQueue<GPanel> dirtyPanels = new GAsyncWorkQueue<>();

    public GRootPanel(final int width, final int height) {
        super(0, 0, 0, width, height);
        // Prepare drawing area.
        GConsole.print(GStrings.NEW_LINE.repeat(height));
        GConsole.flush();
    }

    public void awaitDirty() throws InterruptedException {
        dirtyPanels.awaitWork();
    }

    public void refreshDirty() {
        dirtyPanels.doWork(GPanel::refresh);
    }

    @Override
    public void signalDirty(final GPanel panel) {
        dirtyPanels.queueWork(panel);
    }

    public void draw() {
        GConsole.print(GConsole.cursorUp(height));
        drawPanel();
        GConsole.print(GConsole.cursorDown(height));
        GConsole.print(GConsole.CC_CR);
        GConsole.print(GConsole.resetColor());
        GConsole.flush();
    }
}
