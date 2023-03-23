// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.console;

import com.glitchybyte.glib.concurrent.GEvent;
import com.glitchybyte.glib.concurrent.GEventHandler;
import com.glitchybyte.glib.concurrent.GLock;
import com.glitchybyte.glib.log.GLog;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class GLogPanel extends GPanel {

    private final Deque<String> lines = new ArrayDeque<>();
    private final ReadWriteLock linesLock = new ReentrantReadWriteLock();

    public GLogPanel(final int zOrder, final int y, final int height, final GEventHandler eventHandler) {
        super(zOrder, 0, y, 1, height);
        for (int i = 0; i < height; ++i) {
            lines.offerLast("");
        }
        eventHandler.registerEventHandler(GLog.getLogEventKey(), this::onLogReceived);
    }

    private void onLogReceived(final GEvent event) {
        final String line = event.getDataAsString();
        GLock.writeLocked(linesLock, () -> {
            lines.removeFirst();
            lines.offerLast(line);
        });
        signalDirty();
    }

    @Override
    protected String createImprint() {
        final StringBuilder sb = new StringBuilder();
        GLock.readLocked(linesLock, () -> {
            for (final String line: lines) {
                sb.append(moveToXOffset());
                sb.append(line);
                sb.append(GConsole.clearToEndOfLine());
                sb.append(GConsole.cursorDown(1));
            }
        });
        sb.append(GConsole.cursorUp(height));
        return sb.toString();
    }
}
