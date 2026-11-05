package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.Assets;

/**
 * "DAY n CLEARED" screen: title picture, status card, and three picture
 * buttons.
 */
public class DayClearedPanel extends JPanel {

    private static final String ASSETS = "interface/";
    private static final String MONSTER_PATH = ASSETS + "failed_monster.png";
    private static final String NEXTDAY_PATH = ASSETS + "congrats_nextday.png";
    private static final String MAINMENU_PATH = ASSETS + "congrats_mainmenu.png";
    private static final String LEVELSELECT_PATH = ASSETS + "congrats_levelselect.png";

    private static final Color TINT = new Color(8, 30, 20, 150); // darkens the video
    private static final Color GOLD = new Color(0xFFC107);
    private static final Color GREEN = new Color(0x3A8A5A); // bar track
    private static final Color LIGHT_GREEN = new Color(0x4CC38A);
    private static final Color LABEL = new Color(0xB5C4BA);
    private static final Color LEFT_BG = new Color(18, 50, 38, 215);
    private static final Color RIGHT_BG = new Color(22, 62, 46, 215);

    private static final int LEFT_WIDTH = 340;
    private static final int RIGHT_WIDTH = 580;
    private static final int CARD_HEIGHT = 380;
    private static final int MONSTER_WIDTH = 290;
    private static final int TITLE_WIDTH = 600; // Adjust image width for title as needed
    private static final int BUTTON_WIDTH = 260;

    private static final int GAP_TITLE = 5; // title -> card
    private static final int GAP_BUTTONS = 10; // card -> buttons
    private static final int BUTTON_SPACING = 24;

    /**
     * @param day     day number in the title
     * @param percent 0-100, big bar on the right
     * @param points  score under the bar
     * @param correct correct answers (shown as correct/total)
     * @param total   total questions
     * @param overall 0-100, small bar under the monster (shown as overall/100)
     */
    public DayClearedPanel(int day, int percent, int points, int correct, int total, int overall,
            Runnable onNextDay, Runnable onMainMenu, Runnable onLevelSelect) {
        setOpaque(false);
        setLayout(new GridBagLayout()); // centers the column

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        // Dynamically loads title image per day (e.g. interface/day1_title.png)
        JLabel title = picture(ASSETS + "day" + day + "_title.png", TITLE_WIDTH);

        column.add(title);
        column.add(Box.createVerticalStrut(GAP_TITLE));
        column.add(buildCard(day, percent, points, correct, total, overall));
        column.add(Box.createVerticalStrut(GAP_BUTTONS));
        column.add(buildButtons(onNextDay, onMainMenu, onLevelSelect));

        add(column);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(TINT);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    // ---------- card ----------

    private JPanel buildCard(int day, int percent, int points, int correct, int total, int overall) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        Dimension size = new Dimension(LEFT_WIDTH + RIGHT_WIDTH, CARD_HEIGHT);
        card.setPreferredSize(size);
        card.setMaximumSize(size);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Left: monster and small bar
        JPanel left = filled(LEFT_BG);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(LEFT_WIDTH, CARD_HEIGHT));
        left.add(Box.createVerticalGlue());
        left.add(picture(MONSTER_PATH, MONSTER_WIDTH));
        left.add(Box.createVerticalStrut(24));
        left.add(new Bar(overall, 195, 14));
        left.add(Box.createVerticalStrut(8));
        left.add(text(overall + "/100", new Font("Consolas", Font.PLAIN, 15), LIGHT_GREEN));
        left.add(Box.createVerticalGlue());

        // Right: status report
        JPanel right = filled(RIGHT_BG);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(Box.createVerticalGlue());
        right.add(text("STATUS REPORT", new Font("Consolas", Font.BOLD, 34), Color.WHITE));
        right.add(Box.createVerticalStrut(24));
        right.add(new Bar(percent, 325, 17));
        right.add(Box.createVerticalStrut(6));
        right.add(text(percent + "%", new Font("Consolas", Font.BOLD, 15), GOLD));
        right.add(Box.createVerticalStrut(26));
        right.add(stats(points, correct, total));
        right.add(Box.createVerticalStrut(40));
        right.add(text("Congratulations! Day " + day + " is complete. Please proceed to Day " + (day + 1),
                new Font("Consolas", Font.PLAIN, 11), LABEL));
        right.add(Box.createVerticalGlue());

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.CENTER);
        return card;
    }

    private JPanel stats(int points, int correct, int total) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.add(stat(String.valueOf(points), "POINTS"));
        row.add(stat(correct + "/" + total, "ANSWERED QUESTIONS"));
        return row;
    }

    private JPanel stat(String value, String caption) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(text(value, new Font("Consolas", Font.BOLD, 40), GOLD));
        p.add(text(caption, new Font("Consolas", Font.PLAIN, 11), LABEL));
        return p;
    }

    // ---------- buttons ----------

    private JPanel buildButtons(Runnable onNextDay, Runnable onMainMenu, Runnable onLevelSelect) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, BUTTON_SPACING, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.add(new ImageButton(NEXTDAY_PATH, BUTTON_WIDTH, onNextDay));
        row.add(new ImageButton(MAINMENU_PATH, BUTTON_WIDTH, onMainMenu));
        row.add(new ImageButton(LEVELSELECT_PATH, BUTTON_WIDTH, onLevelSelect));
        return row;
    }

    // ---------- helpers ----------

    private JLabel text(String s, Font font, Color color) {
        JLabel label = new JLabel(s);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel picture(String path, int width) {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        BufferedImage img = Assets.scaled(path, width);
        if (img != null) {
            label.setIcon(new ImageIcon(img));
        }
        return label;
    }

    /** A panel with a semi-transparent fill, so the video shows faintly through. */
    private JPanel filled(Color color) {
        return new JPanel() {
            {
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(color);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
    }

    /** Flat bar: gold filled part over a green track. */
    private static class Bar extends JComponent {
        private final double fraction;

        Bar(int percent, int width, int height) {
            this.fraction = Math.max(0, Math.min(100, percent)) / 100.0;
            Dimension size = new Dimension(width, height);
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(GREEN);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(GOLD);
            g.fillRect(0, 0, (int) (getWidth() * fraction), getHeight());
        }
    }
}