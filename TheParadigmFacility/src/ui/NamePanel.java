package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;

// Popup that asks for the player's name

public class NamePanel extends PopupWindow {
    private static final int BOX_WIDTH = 620;
    private static final int BOX_HEIGHT = 340;
    private static final int MAX_NAME = 15; // keeps the leaderboard rows tidy. max name = 15 characters'

    public NamePanel(Consumer<String> onSubmit, Runnable onClose) {
        super(BOX_WIDTH, BOX_HEIGHT, onClose);

        JLabel heading = new JLabel("Welcome to Paradigm Facility");
        heading.setFont(Theme.bold(26));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("\u2022 /* Enter your name to start */");
        subtitle.setFont(Theme.plain(18));
        subtitle.setForeground(Theme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = new JTextField();
        field.setFont(Theme.plain(16));
        field.setBackground(Theme.FIELD);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        field.setMaximumSize(new Dimension(BOX_WIDTH, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        ActionListener submit = e -> {
            // commas would break the scoreboard.csv format
            String name = field.getText().trim().replace(",", "");
            if (name.length() > MAX_NAME) {
                name = name.substring(0, MAX_NAME);
            }
            if (!name.isEmpty()) {
                onSubmit.accept(name);
            }
        };
        field.addActionListener(submit); // Enter key submits

        content.add(heading);
        content.add(Box.createVerticalStrut(10));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(40));
        content.add(field);
    }
}