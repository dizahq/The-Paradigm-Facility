package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Title screen: PNG title and a play button. Pure AWT/Swing. */
public class TitlePanel extends JPanel {

    private static final String TITLE_PATH =
            "TheParadigmFacility/assets/interface/title.png"; // <- your PNG path
    private static final int TITLE_WIDTH = 1100;               // <- title size
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
        pressToStart.setFont(new Font("Consolas", Font.PLAIN, 12));
        pressToStart.setForeground(new Color(0x8A938A));
        pressToStart.setAlignmentX(Component.CENTER_ALIGNMENT);

        column.add(title);
        column.add(Box.createVerticalStrut(120));
        column.add(play);
        column.add(Box.createVerticalStrut(10));
        column.add(pressToStart);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;  // stick to the top instead of the center
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(TOP_OFFSET, 0, 0, 0);
        add(column, gbc);
    }

    private JLabel buildTitle() {
        File file = new File(TITLE_PATH);
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) throw new java.io.IOException("Unsupported image");
            int h = img.getHeight() * TITLE_WIDTH / img.getWidth();
            Image scaled = img.getScaledInstance(TITLE_WIDTH, h, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        } catch (Exception e) {
            System.err.println("Title not found at: " + file.getAbsolutePath());
            JLabel fallback = new JLabel("PARADIGM FACILITY");
            fallback.setFont(new Font("Consolas", Font.BOLD, 56));
            fallback.setForeground(Color.WHITE);
            return fallback;
        }
    }

    /** Circular play button drawn with Graphics2D, with a hover color. */
    private static class PlayButton extends JComponent {
        private static final Color NORMAL = new Color(0x6B746B);
        private static final Color HOVER = new Color(0xFF4D4D);
        private boolean hover;

        PlayButton(Runnable onClick) {
            setPreferredSize(new Dimension(80, 80));
            setMaximumSize(new Dimension(80, 80));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? HOVER : NORMAL);
            g2.setStroke(new BasicStroke(4f));
            g2.drawOval(4, 4, 72, 72);
            g2.fillPolygon(new int[]{32, 32, 58}, new int[]{24, 56, 40}, 3);
            g2.dispose();
        }
    }
}