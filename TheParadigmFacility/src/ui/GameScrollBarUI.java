package ui;

// Custom class for Help Scrollbar kay pangit ang default 

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class GameScrollBarUI extends BasicScrollBarUI {
    private static final Color TRACK = new Color(0xEBEBEB);
    private static final Color THUMB = new Color(0xC4C4C4);
    private static final Color BUTTON = new Color(0xDCDCDC);
    private static final Color EDGE = new Color(0x808080); // outline of thumb and buttons
    private static final Color ARROW = new Color(0x222222);
    private static final int EDGE_THICKNESS = 2;
    private static final int ARROW_SIZE = 5;
    public static final int WIDTH = 20;

    // A filled rectangle w solid outline
    private static void box(Graphics g, int x, int y, int w, int h, Color fill) {
        g.setColor(fill);
        g.fillRect(x, y, w, h);
        g.setColor(EDGE);
        for (int i = 0; i < EDGE_THICKNESS; i++) {
            g.drawRect(x + i, y + i, w - 1 - 2 * i, h - 1 - 2 * i);
        }
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new ArrowButton(orientation);
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new ArrowButton(orientation);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
        g.setColor(TRACK);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
        if (bounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        box(g, bounds.x, bounds.y, bounds.width, bounds.height, THUMB);
    }

    // Square button w an outline and a dark triangle
    private static class ArrowButton extends BasicArrowButton {
        ArrowButton(int orientation) {
            super(orientation, BUTTON, BUTTON, ARROW, BUTTON);
            setPreferredSize(new Dimension(WIDTH, WIDTH));
        }

        @Override
        public void paint(Graphics g) {
            box(g, 0, 0, getWidth(), getHeight(), BUTTON);
            paintTriangle(g, (getWidth() - ARROW_SIZE) / 2, (getHeight() - ARROW_SIZE) / 2, ARROW_SIZE, getDirection(),
                    true);
        }
    }

}
