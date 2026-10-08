package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Base class for every popup: NamePanel, PausePanel, InfoPanel. Dimmed backdrop, centered box, title bar w a red X.
// Subclasses only add their own components

public abstract class PopupWindow extends JPanel {
    private static final int TITLEBAR_HEIGHT = 40;

    // Dark area inside the box. Subclasses add their components here.
    protected final JPanel content = new JPanel();

    // No title text, dark green background (used by NamePanel)
    protected PopupWindow(int width, int height, Runnable onClose) {
        this("", width, height, Theme.PANEL, onClose);
    }

    protected PopupWindow(String title, int width, int height, Color background, Runnable onClose) {
        setOpaque(false);
        setLayout(new GridBagLayout()); // centers the box

        content.setBackground(background);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Dimension size = new Dimension(width, height);
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(size);
        box.setMaximumSize(size);
        box.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        box.add(buildTitleBar(title, width, onClose), BorderLayout.NORTH);
        box.add(content, BorderLayout.CENTER);

        add(box);
    }

    private JComponent buildTitleBar(String title, int width, Runnable onClose) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.TITLEBAR);
        bar.setPreferredSize(new Dimension(width, TITLEBAR_HEIGHT));

        if (!title.isEmpty()) {
            JLabel label = new JLabel(title);
            label.setFont(Theme.bold(18));
            label.setForeground(Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
            bar.add(label, BorderLayout.WEST);
        }

        JPanel closeWrap = new JPanel();
        closeWrap.setOpaque(false);
        closeWrap.add(new CloseX(onClose));

        bar.add(closeWrap, BorderLayout.EAST);
        return bar;
    }

    // Dim everything behind the box
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Theme.BACKDROP);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    // Red "X" close button, top right of the title bar
    private static class CloseX extends JComponent {
        private boolean hover;

        CloseX(Runnable onClick) {
            Dimension size = new Dimension(36, 36);
            setPreferredSize(size);
            setMaximumSize(size);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    onClick.run();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? Color.WHITE : Theme.DANGER);
            g2.setFont(Theme.bold(20));

            String text = "X";
            int tw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (getWidth() - tw) / 2, getHeight() / 2 + 7);
            g2.dispose();
        }
    }
}
