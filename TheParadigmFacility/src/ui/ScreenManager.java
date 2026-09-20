package ui;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 * Holds the one shared BackgroundLoop at the bottom and shows
 * one Swing panel on top of it at a time.
 */
public class ScreenManager extends JLayeredPane {

    private final BackgroundLoop background = new BackgroundLoop();
    private JComponent current;

    public ScreenManager() {
        add(background, DEFAULT_LAYER);
    }

    /** Replace the panel shown above the video. */
    public void show(JComponent panel) {
        if (current != null) {
            remove(current);
        }
        current = panel;
        panel.setOpaque(false); // let the video show through
        add(panel, PALETTE_LAYER);
        doLayout();
        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {
        background.setBounds(0, 0, getWidth(), getHeight());
        if (current != null) {
            current.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    public void dispose() {
        background.dispose();
    }
}