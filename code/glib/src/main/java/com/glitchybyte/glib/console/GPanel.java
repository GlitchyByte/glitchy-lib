// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.console;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class GPanel {

    private GPanel parent = null;
    public final int zOrder;
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    private final List<GPanel> panels = new ArrayList<>();
    private String imprint = "";

    public GPanel(final int zOrder, final int x, final int y, final int width, final int height) {
        this.zOrder = zOrder;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private boolean willFit(final GPanel panel) {
        return ((panel.x + panel.width) <= width) &&
                ((panel.y + panel.height) <= height);
    }

    public void addPanel(final GPanel panel) {
        if (!willFit(panel)) {
            throw new IllegalArgumentException("Panel doesn't fit parent!");
        }
        panel.parent = this;
        panels.add(panel);
        panels.sort(Comparator.comparingInt(p -> p.zOrder));
    }

    protected int getXOffset() {
        return parent == null ? x : x + parent.getXOffset();
    }

    protected int getYOffset() {
        return parent == null ? y : y + parent.getYOffset();
    }

    protected String moveToXOffset() {
        return GConsole.CC_CR + GConsole.cursorRight(getXOffset());
    }

    /**
     * Creates the panel imprint.
     *
     * <p>This method MUST return the cursor to the same line. Horizontal
     * position is not important.
     *
     * @return A complete imprint of the panel.
     */
    protected abstract String createImprint();

    protected void signalDirty() {
        signalDirty(this);
    }

    public void signalDirty(final GPanel panel) {
        parent.signalDirty(panel);
    }

    public void refresh() {
        imprint = createImprint();
        panels.forEach(GPanel::refresh);
    }

    protected void drawPanel() {
        final int yOffset = getYOffset();
        GConsole.print(GConsole.resetColor());
        GConsole.print(GConsole.cursorDown(yOffset));
        GConsole.print(imprint);
        GConsole.print(GConsole.cursorUp(yOffset));
        panels.forEach(GPanel::drawPanel);
    }

    protected String fill(final char glyph, final Integer fgColor, final Integer bgColor) {
        final String ch = Character.toString(glyph);
        final StringBuilder sb = new StringBuilder();
        if (fgColor != null) {
            sb.append(GConsole.foregroundColor(fgColor));
        }
        if (bgColor != null) {
            sb.append(GConsole.backgroundColor(bgColor));
        }
        for (int i = 0; i < height; ++i) {
            sb.append(moveToXOffset());
            sb.append(ch.repeat(width));
            sb.append(GConsole.cursorDown(1));
        }
        sb.append(GConsole.cursorUp(height));
        return sb.toString();
    }

    protected String fillBackground(final Integer bgColor) {
        return fill(' ', null, bgColor);
    }
}
