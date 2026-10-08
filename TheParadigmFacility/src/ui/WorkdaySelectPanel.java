package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

// Workday selection screen: a scrolling list of workday cards, a progress bar, and return / leaderboard buttons. 

public class WorkdaySelectPanel extends JPanel {
    // One card. Done/total is shown on the right; locked cards cannot be clicked
    public record Item(String title, String subtitle, int done, int total, boolean unlocked) {
    }

    private static final String RETURN_BTN = "interface/returnBtn2.png";
    private static final String LEADERBOARD_BTN = "interface/leaderboardBtn.png";
    private static final int RETURN_BTN_WIDTH = 165;
    private static final int LEADERBOARD_BTN_WIDTH = 200;

    private static final int CARD_WIDTH = 975;
    private static final int CARD_HEIGHT = 98;
    private static final int CARD_GAP = 25;
    private static final int LIST_TOP_GAP = 54; // space between the header and the first card
    private static final int LIST_BOTTOM_GAP = 20;

    private static final int BAR_WIDTH = 342;
    private static final int BAR_HEIGHT = 17;

    private static final Color GOLD = new Color(0xF5B800);
    private static final Color BAR_TRACK = new Color(0x3B7A54);

    private static final Color UNLOCKED_BG = new Color(0x17402F);
    private static final Color UNLOCKED_HOVER = new Color(0x1F5440);
    private static final Color LOCKED_BG = new Color(0x474747);
    private static final Color LOCKED_TITLE = new Color(0xE6E6E6);
    private static final Color LOCKED_TEXT = new Color(0x8A8A8A);
    private static final Color EDGE = new Color(0xD8D8D8);

    // items: workdays, in order
    // onSelect: called w the workday index (0 to 6) when an unlocked card is
    // clicked

    public WorkdaySelectPanel(List<Item> items, IntConsumer onSelect, Runnable onReturn, Runnable onLeaderboard) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, LIST_BOTTOM_GAP, 0));

        add(buildHeader(items, onReturn, onLeaderboard), BorderLayout.NORTH);
        add(buildList(items, onSelect), BorderLayout.CENTER);
    }

    private JPanel buildHeader(List<Item> items, Runnable onReturn, Runnable onLeaderboard) {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.setOpaque(false);
        buttons.add(new ImageButton(RETURN_BTN, RETURN_BTN_WIDTH, onReturn));
        buttons.add(new ImageButton(LEADERBOARD_BTN, LEADERBOARD_BTN_WIDTH, onLeaderboard));

        int done = 0;
        int total = 0;
        for (Item item : items) {
            done += item.done();
            total += item.total();
        }
        int percent = total == 0 ? 0 : done * 100 / total;

        JPanel progressWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        progressWrap.setOpaque(false);
        progressWrap.setBorder(BorderFactory.createEmptyBorder(17, 0, 0, 0));
        progressWrap.add(new Progress(percent));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(47, 62, 0, 73));
        header.add(buttons, BorderLayout.WEST);
        header.add(progressWrap, BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildList(List<Item> items, IntConsumer onSelect) {
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                list.add(Box.createVerticalStrut(CARD_GAP));
            }
            list.add(new Card(i, items.get(i), onSelect));
        }

        // the left padding balances the scrollbar so the cards stay centered
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(LIST_TOP_GAP, 0, LIST_BOTTOM_GAP, 0);
        content.add(list, gbc);

        JScrollPane scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);

        // repaint fully while scrolling so the animated background doesnt smear
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        JScrollBar bar = scroll.getVerticalScrollBar();
        // bar.setUI(new GameScrollBarUI());
        // bar.setPreferredSize(new Dimension(GameScrollBarUI.WIDTH, 0));
        bar.setPreferredSize(new Dimension(0, 0)); // invisible, but wheel still works
        bar.setUnitIncrement(32); // mouse wheel speed
        return scroll;
    }

    // Draws text with its vertical center at centerY. If alignRight, x is the
    // righte edge
    private static void drawText(Graphics2D g2, String text, Font font, Color color, int x, int centerY,
            boolean alignRight) {
        g2.setFont(font);
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        int y = centerY - fm.getHeight() / 2 + fm.getAscent();
        g2.drawString(text, alignRight ? x - fm.stringWidth(text) : x, y);
    }

    private static void smooth(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    // One workday card
    private static class Card extends JComponent {
        private final Item item;
        private boolean hover;

        Card(int index, Item item, IntConsumer onSelect) {
            this.item = item;

            Dimension size = new Dimension(CARD_WIDTH, CARD_HEIGHT);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);

            if (item.unlocked()) {
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
                        onSelect.accept(index);
                    }
                });
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            boolean unlocked = item.unlocked();

            g2.setColor(!unlocked ? LOCKED_BG : hover ? UNLOCKED_HOVER : UNLOCKED_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(EDGE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

            boolean hasSubtitle = !item.subtitle().isEmpty();
            drawText(g2, item.title(), Theme.bold(24), unlocked ? Color.WHITE : LOCKED_TITLE, 52,
                    hasSubtitle ? 36 : getHeight() / 2, false);

            if (hasSubtitle) {
                drawText(g2, item.subtitle(), Theme.plain(15), unlocked ? Theme.MUTED : LOCKED_TEXT, 52, 67, false);
            }
            drawText(g2, item.done() + "/" + item.total(), Theme.bold(17), unlocked ? GOLD : LOCKED_TEXT,
                    getWidth() - 37, getHeight() / 2, true);

            g2.dispose();
        }
    }

    // Progress bar w percentage under its right end
    private static class Progress extends JComponent {
        private final int percent;

        Progress(int percent) {
            this.percent = Math.max(0, Math.min(100, percent));
            Dimension size = new Dimension(BAR_WIDTH, BAR_HEIGHT + 34);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);

            g2.setColor(BAR_TRACK);
            g2.fillRect(0, 0, BAR_WIDTH, BAR_HEIGHT);
            g2.setColor(GOLD);
            g2.fillRect(0, 0, BAR_WIDTH * percent / 100, BAR_HEIGHT);

            drawText(g2, percent + "%", Theme.bold(16), GOLD, BAR_WIDTH, BAR_HEIGHT + 17, true);
            g2.dispose();
        }
    }
}
