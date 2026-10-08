package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.Assets;

/** Title screen: PNG title and a play button. Pure AWT/Swing. */
public class TitlePanel extends JPanel {

    private static final String TITLE_PATH = "interface/title.png"; // <- your PNG path
    private static final int TITLE_WIDTH = 1100; // <- title size
    private static final int TOP_OFFSET = 170; // space above the title; smaller = title higher

    public TitlePanel(Runnable onStart) {
        setOpaque(false);
        setLayout(new GridBagLayout()); // centers the single child

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        JLabel title = buildTitle();
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        PlayButton play = new PlayButton(onStart);
        play.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pressToStart = new JLabel("PRESS TO START");
        pressToStart.setFont(Theme.plain(12));
        pressToStart.setForeground(Theme.MUTED);
        pressToStart.setAlignmentX(Component.CENTER_ALIGNMENT);

        column.add(title);
        column.add(Box.createVerticalStrut(120));
        column.add(play);
        column.add(Box.createVerticalStrut(10));
        column.add(pressToStart);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH; // stick to the top instead of the center
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(TOP_OFFSET, 0, 0, 0);
        add(column, gbc);
    }

    private JLabel buildTitle() {
        BufferedImage img = Assets.scaled(TITLE_PATH, TITLE_WIDTH);
        if (img != null) {
            return new JLabel(new ImageIcon(img));
        }
        JLabel fallback = new JLabel("PARADIGM FACILITY");
        fallback.setFont(Theme.bold(56));
        fallback.setForeground(Color.WHITE);
        return fallback;
    }

    /** Circular play button drawn with Graphics2D, with a hover color. */
    private static class PlayButton extends JComponent {
        private boolean hover;

        PlayButton(Runnable onClick) {
            setPreferredSize(new Dimension(80, 80));
            setMaximumSize(new Dimension(80, 80));
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
            g2.setColor(hover ? Theme.HOVER_RED : Theme.IDLE);
            g2.setStroke(new BasicStroke(4f));
            g2.drawOval(4, 4, 72, 72);
            g2.fillPolygon(new int[] { 32, 32, 58 }, new int[] { 24, 56, 40 }, 3);
            g2.dispose();
        }
    }
}