package ui;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 * Holds the one shared BackgroundLoop at the bottom, shows one main
 * screen on top of it, and can layer an overlay (settings, name entry,
 * pause menu, ...) above that without removing the main screen.
 */
public class ScreenManager extends JLayeredPane {

    private final BackgroundLoop background = new BackgroundLoop();
    private JComponent current;
    private JComponent overlay;

    public ScreenManager() {
        add(background, DEFAULT_LAYER);
    }

    /** Replace the main screen (title, menu, gameplay, ...). */
    public void show(JComponent panel) {
        if (current != null) {
            remove(current);
        }
        current = panel;
        panel.setOpaque(false); // let the video show through
        add(panel, PALETTE_LAYER);
        refresh();
    }

    /** Show a panel above the current screen without removing it. */
    public void showOverlay(JComponent panel) {
        if (overlay != null) {
            remove(overlay);
        }
        overlay = panel;
        add(panel, MODAL_LAYER);
        refresh();
    }

    /** Remove whatever overlay is showing, revealing the screen underneath. */
    public void hideOverlay() {
        if (overlay != null) {
            remove(overlay);
            overlay = null;
            refresh();
        }
    }

    private void refresh() {
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
        if (overlay != null) {
            overlay.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    public void dispose() {
        background.dispose();
    }
}