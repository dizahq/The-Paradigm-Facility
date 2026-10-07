package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SettingsPanel extends JPanel {
    private static final Color TITLE_COLOR = Color.white;
    private static final Color SUBTITLE_COLOR = new Color(0x8FA096);
    private static final Color LINE_COLOR = new Color(255, 255, 255, 60);
    private static final Color ACCENT_COLOR = new Color(0x4ADE80);

    private static final String ASSET = "TheParadigmFacility/assets/interface/mainMenubtn.png";
    private static final int MAIN_MENU_BTN_WIDTH = 220;

    private static final int SIDE_PADDING = 70;
    private static final int TOP_PADDING = 60;
    private static final int ROW_GAP = 26;

    public SettingsPanel(Runnable onBack, Consumer<Boolean> onMusicToggle, Runnable onHelp, Runnable onAdmin,
            Runnable onAbout) {

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(TOP_PADDING, SIDE_PADDING, TOP_PADDING, SIDE_PADDING));

        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));

        rows.add(buildRow("Music", "facility's background  noise", new ToggleSwitch(true, onMusicToggle)));
        rows.add(Box.createVerticalStrut(ROW_GAP));

        rows.add(buildRow("Help", "helps you navigate your onboarding process", new CodeLink("print[help]", onHelp)));
        rows.add(Box.createVerticalStrut(ROW_GAP));

        rows.add(buildRow("Admin", "people that controls the facility", new CodeLink("print[admin]", onAdmin)));
        rows.add(Box.createVerticalStrut(ROW_GAP));

        rows.add(buildRow("About", "the facility's purpose and history", new CodeLink("print[about]", onAbout)));
        rows.add(Box.createVerticalStrut(ROW_GAP));

        add(rows, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        bottom.add(new ImageButton(ASSET, MAIN_MENU_BTN_WIDTH, onBack));
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildRow(String title, String subtitle, JComponent action) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 22));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        titleLabel.setFont(new Font("Consolas", Font.PLAIN, 13));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textBlock.add(titleLabel);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(subtitleLabel);

        JPanel actionWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionWrap.setOpaque(false);
        actionWrap.add(action);

        row.add(textBlock, BorderLayout.WEST);
        row.add(actionWrap, BorderLayout.EAST);

        JPanel withLine = new JPanel();
        withLine.setOpaque(false);
        withLine.setLayout(new BoxLayout(withLine, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        withLine.add(row);
        withLine.add(Box.createVerticalStrut(14));
        withLine.add(new Divider());

        return withLine;
    }

    private static class Divider extends JComponent {
        public Divider() {
            setPreferredSize(new java.awt.Dimension(10, 1));
            setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 1));
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            g.setColor(LINE_COLOR);
            g.drawLine(0, 0, getWidth(), 0);
        }
    }

    private static class CodeLink extends JComponent {
        private final String text;
        private boolean hover;

        CodeLink(String text, Runnable onClick) {
            this.text = text;
            setFont(new Font("Consolas", Font.PLAIN, 15));
            Dimension size = new Dimension(getFontMetrics(getFont()).stringWidth(text) + 4, 22);
            setPreferredSize(size);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    onClick.run();
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("Consolas", Font.PLAIN, 16));
            g.setColor(hover ? ACCENT_COLOR : SUBTITLE_COLOR);
            g.drawString(text, 0, getHeight() - 4);
        }
    }

    private static class ToggleSwitch extends JComponent {
        private static final int W = 46, H = 24;
        private boolean on;
        private final Consumer<Boolean> onToggle;

        ToggleSwitch(boolean initial, Consumer<Boolean> onToggle) {
            this.on = initial;
            this.onToggle = onToggle;

            setPreferredSize(new Dimension(W, H));
            setMaximumSize(new Dimension(W, H));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    on = !on;
                    onToggle.accept(on);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            g.setColor(on ? ACCENT_COLOR : LINE_COLOR);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            g.setColor(Color.WHITE);
            int knobX = on ? getWidth() - 22 : 2;
            g.fillOval(knobX, 2, 21, 21);
        }
    }

}
