package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Leaderboard screen: records top 5 players, with gold, silver, and bronze points for the top 3.

public class LeaderBoard extends JPanel {
    private static final String RETURN_BTN = "interface/returnBtn2.png";
    private static final int RETURN_BTN_WIDTH = 165;

    private static final int PLACES = 5;
    private static final int ROW_WIDTH = 973;
    private static final int ROW_HEIGHT = 76;
    private static final int ROW_GAP = 17;

    private static final Color ROW_BG = new Color(0x173D2D);
    private static final Color GOLD = new Color(0xF5B800);
    private static final Color SILVER = new Color(0xC0C4C0);
    private static final Color BRONZE = new Color(0xB8862B);

    // Players sorted in descending (at most 5)
    public LeaderBoard(List<Map.Entry<String, Integer>> top, Runnable onReturn) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;

        // return button, top left
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(47, 62, 0, 0);
        add(new ImageButton(RETURN_BTN, RETURN_BTN_WIDTH, onReturn), gbc);

        // title, centered
        JLabel title = new JLabel("LEADERBOARD");
        title.setFont(Theme.bold(40));
        title.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(16, 0, 41, 0);
        add(title, gbc);

        // five rows, stuck to the top of the remaining space
        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        for (int i = 0; i < PLACES; i++) {
            if (i > 0) {
                rows.add(Box.createVerticalStrut(ROW_GAP));
            }
            rows.add(new Row(i + 1, i < top.size() ? top.get(i) : null));
        }
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(rows, gbc);
    }

    // 14000 becomes "14 000"
    private static String formatPoints(int points) {
        return String.format(Locale.US, "%,d", points).replace(',', ' ');
    }

    // One leaderboard row: rank, name, points. A null entry draws an empty place.
    private static class Row extends JComponent {
        private final String rank;
        private final String name;
        private final String points;
        private final Color pointsColor;

        Row(int place, Map.Entry<String, Integer> entry) {
            this.rank = "#" + place;
            this.name = entry == null ? "---" : entry.getKey();
            this.points = entry == null ? "" : formatPoints(entry.getValue()) + " PTS";
            this.pointsColor = place == 1 ? GOLD : place == 2 ? SILVER : place == 3 ? BRONZE : Color.WHITE;

            Dimension size = new Dimension(ROW_WIDTH, ROW_HEIGHT);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setColor(ROW_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            draw(g2, rank, Theme.bold(24), Theme.MUTED, 36, false);
            draw(g2, name, Theme.bold(26), Color.WHITE, 101, false);
            draw(g2, points, Theme.plain(24), pointsColor, getWidth() - 37, true);

            g2.dispose();
        }

        // Draws text vertically centered. If alignRight, x is the right edge of the
        // text
        private void draw(Graphics2D g2, String text, Font font, Color color, int x, boolean alignRight) {
            g2.setFont(font);
            g2.setColor(color);
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, alignRight ? x - fm.stringWidth(text) : x, y);
        }
    }
}
