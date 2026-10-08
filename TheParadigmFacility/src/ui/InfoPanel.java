package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InfoPanel extends PopupWindow {
    private static final int BOX_WIDTH = 750;
    private static final int BOX_HEIGHT = 340;
    private static final int TEXT_SIZE = 18;

    // Add text anytime. Box scrolls when it does not fit.
    // Help Popup
    private static final String HELP_TEXT = "Clear all 7 workdays by passing programming paradigm quizzes, earning a position in the "
            + "leaderboard, and in the company while keeping bugs below 3.\n\n"
            + "The game spans for 7 days. Each day introduces a new programming paradigm "
            + "and scales up the points per question.\n\n"
            + "Correct answers earn full points and add to a streak.\n"
            + "Wrong answers cost half the question\u2019s points, gains 1 bug, and resets the streak."
            + "The game ends when 3 bugs is earned.";

    // About Popup
    private static final String ABOUT_TEXT = "Welcome to the Paradigm Facility, where badly written code comes alive as terrifying "
            + "spaghetti code monsters. Contained inside locked packages, these creatures "
            + "threaten to corrupt the entire tech stack. As a new software engineer, you "
            + "must pass rigorous paradigm quizzes to unlock, refactor, and delete them. \n\n"
            + "Survive the 7-day probationary training, keep your job, and earn your "
            + "permanent position!";

    // Credits Popup
    private static final String CREDITS_TEXT = "Programmers: \n"
            + "Catenza, Dizahlene \n"
            + "Chu, Joshua\n"
            + "Cristino, Allyssa";

    public static InfoPanel help(Runnable onClose) {
        return new InfoPanel("HELP", HELP_TEXT, onClose);
    }

    public static InfoPanel about(Runnable onClose) {
        return new InfoPanel("ABOUT", ABOUT_TEXT, onClose);
    }

    public static InfoPanel credits(Runnable onClose) {
        return new InfoPanel("CREDITS", CREDITS_TEXT, onClose);
    }

    private InfoPanel(String title, String body, Runnable onClose) {
        super(title, BOX_WIDTH, BOX_HEIGHT, Color.BLACK, onClose);

        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 12));

        JTextArea text = new JTextArea("printing information...\n\n" + body);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setFont(Theme.plain(TEXT_SIZE));
        text.setForeground(Color.WHITE);
        text.setBackground(Color.BLACK);
        text.setCaretPosition(0); // start at the top

        JScrollPane scroll = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.BLACK);

        content.add(scroll, BorderLayout.CENTER);
    }
}
