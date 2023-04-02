// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.console.display;

import com.glitchybyte.glib.console.GConsole;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Console UI panel.
 */
public abstract class GPanel {

    /**
     * Panel Z position. Lower number gets drawn first.
     */
    public final int zOrder;

    /**
     * X position.
     */
    public final int x;

    /**
     * Y position.
     */
    public final int y;

    /**
     * Panel width.
     */
    public final int width;

    /**
     * Panel height.
     */
    public final int height;

    /**
     * Root panel that contains all panels.
     */
    protected GRootPanel rootPanel = null;

    private GPanel parent = null;
    private final List<GPanel> panels = new ArrayList<>();
    private String imprint = "";

    /**
     * Creates a console panel.
     *
     * @param zOrder Panel Z position. Lower number gets drawn first.
     * @param x X position.
     * @param y Y position.
     * @param width Panel width.
     * @param height Panel height.
     */
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

    /**
     * Adds a child panel to this panel.
     *
     * @param panel Child panel.
     */
    public void addPanel(final GPanel panel) {
        if (!willFit(panel)) {
            throw new IllegalArgumentException("Panel doesn't fit parent!");
        }
        panel.rootPanel = rootPanel;
        panel.parent = this;
        panels.add(panel);
        panels.sort(Comparator.comparingInt(p -> p.zOrder));
    }

    /**
     * Returns the distance from the left of the screen to the left side of this panel.
     *
     * @return The distance from the left of the screen to the left side of this panel.
     */
    protected int getXOffset() {
        return parent == null ? x : x + parent.getXOffset();
    }

    /**
     * Returns the distance from the top of the root panel the top side of this panel.
     *
     * @return The distance from the top of the root panel the top side of this panel.
     */
    protected int getYOffset() {
        return parent == null ? y : y + parent.getYOffset();
    }

    /**
     * Returns a {@code String} to move the cursor to the X offset.
     *
     * @return A {@code String} to move the cursor to the X offset.
     */
    protected String moveToXOffset() {
        return com.glitchybyte.glib.console.GConsole.CC_CR + com.glitchybyte.glib.console.GConsole.cursorRight(getXOffset());
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

    /**
     * Signals this panel's contents have changed and needs to be refreshed.
     */
    public void signalDirty() {
        rootPanel.signalDirty(this);
    }

    /**
     * Refreshes the imprint.
     */
    public void refresh() {
        imprint = createImprint();
    }

    /**
     * Refreshes this panel and all its children.
     */
    public void refreshAll() {
        refresh();
        panels.forEach(GPanel::refreshAll);
    }

    /**
     * Draws the panel.
     */
    protected void drawPanel() {
        final int yOffset = getYOffset();
        com.glitchybyte.glib.console.GConsole.print(com.glitchybyte.glib.console.GConsole.resetColor());
        com.glitchybyte.glib.console.GConsole.print(com.glitchybyte.glib.console.GConsole.cursorDown(yOffset));
        com.glitchybyte.glib.console.GConsole.print(imprint);
        com.glitchybyte.glib.console.GConsole.print(com.glitchybyte.glib.console.GConsole.cursorUp(yOffset));
        panels.forEach(GPanel::drawPanel);
    }

    /**
     * Convenience method to fill the panel area with a glyph and colors.
     *
     * @param glyph Glyph to fill the area with.
     * @param fgColor Foreground color.
     * @param bgColor Background color.
     * @return A {@code String} to fill the panel area with a glyph and colors.
     */
    protected String fill(final char glyph, final Integer fgColor, final Integer bgColor) {
        final String ch = Character.toString(glyph);
        final StringBuilder sb = new StringBuilder();
        if (fgColor != null) {
            sb.append(com.glitchybyte.glib.console.GConsole.foregroundColor(fgColor));
        }
        if (bgColor != null) {
            sb.append(com.glitchybyte.glib.console.GConsole.backgroundColor(bgColor));
        }
        for (int i = 0; i < height; ++i) {
            sb.append(moveToXOffset());
            sb.append(ch.repeat(width));
            sb.append(com.glitchybyte.glib.console.GConsole.cursorDown(1));
        }
        sb.append(GConsole.cursorUp(height));
        return sb.toString();
    }

    /**
     * Convenience method to fill the panel with a background color.
     *
     * @param bgColor Background color.
     * @return A {@code String} to fill the panel area with a background color.
     */
    protected String fillBackground(final Integer bgColor) {
        return fill(' ', null, bgColor);
    }
}
