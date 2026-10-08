package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Settings screen: A sound switch + Help, Admin, About

public class SettingsPanel extends JPanel {
    private static final String RETURN_BTN = "interface/mainMenubtn.png";
    private static final int RETURN_BTN_WIDTH = 190;

    private static final int SIDE_PADDING = 105;
    private static final int TOP_PADDING = 120;
    private static final int BOTTOM_PADDING = 50;
    private static final int ROW_GAP = 36; // space between rows
    private static final int DIVIDER_GAP = 24; // space between a row's text and its divider line

    public SettingsPanel(boolean soundOn, Consumer<Boolean> onSoundToggle, Runnable onHelp, Runnable onAdmin,
            Runnable onAbout, Runnable onBack) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(TOP_PADDING, SIDE_PADDING, BOTTOM_PADDING, SIDE_PADDING));

        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));

        rows.add(buildRow("Sound", "facility's background noise", new ToggleSwitch(soundOn, onSoundToggle)));
        rows.add(Box.createVerticalStrut(ROW_GAP));
        rows.add(buildLinkRow("Help", "helps you navigate your onboarding process", "print(help)", onHelp));
        rows.add(Box.createVerticalStrut(ROW_GAP));
        rows.add(buildLinkRow("Admin", "people that controls the facility", "print(admin)", onAdmin));
        rows.add(Box.createVerticalStrut(ROW_GAP));
        rows.add(buildLinkRow("About", "history of the facility", "print(about)", onAbout));

        add(rows, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(new ImageButton(RETURN_BTN, RETURN_BTN_WIDTH, onBack));
        add(bottom, BorderLayout.SOUTH);
    }

    // A row with a control on the right (like sound switch)
    private JPanel buildRow(String title, String subtitle, JComponent action) {
        return withDivider(buildHeader(title, subtitle, action));
    }

    // A row where the whole text area is clickable, with a green link on the right
    private JPanel buildLinkRow(String title, String subtitle, String linkText, Runnable onClick) {
        JLabel link = label(linkText, Theme.plain(16), Theme.ACCENT);
        JPanel header = buildHeader(title, subtitle, link);

        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                link.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                link.setForeground(Theme.ACCENT);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                link.setForeground(Theme.ACCENT);
                onClick.run();
            }
        });

        return withDivider(header);
    }

    // Title and subtitle on the left, the action centered vertically on the right
    private JPanel buildHeader(String title, String subtitle, JComponent action) {
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(label(title, Theme.bold(28), Color.WHITE));
        text.add(Box.createVerticalStrut(4));
        text.add(label(subtitle, Theme.plain(14), Theme.MUTED));

        JPanel actionWrap = new JPanel(new GridBagLayout()); // centers the action vertically
        actionWrap.setOpaque(false);
        actionWrap.add(action);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(text, BorderLayout.WEST);
        header.add(actionWrap, BorderLayout.EAST);
        return header;
    }

    // Puts a divider line under header
    private JPanel withDivider(JPanel header) {
        JPanel line = new JPanel();
        line.setBackground(Theme.MUTED);
        line.setPreferredSize(new Dimension(1, 2));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(header);
        row.add(Box.createVerticalStrut(DIVIDER_GAP));
        row.add(line);

        // keep the row at its natural height so the layout doesnt stretch it
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    private static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
