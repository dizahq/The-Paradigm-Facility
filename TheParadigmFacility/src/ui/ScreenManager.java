package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;

public class ScreenManager extends JLayeredPane {

    private final BackgroundLoop background = new BackgroundLoop();
    private JComponent current;
    private JComponent overlay;

    public ScreenManager() {
        add(background, DEFAULT_LAYER);
    }

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

        // Block input events from reaching components behind the overlay
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });

        panel.addMouseWheelListener((MouseWheelEvent e) -> e.consume());

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
