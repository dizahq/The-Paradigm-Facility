package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Popup that prints the Help, About or Credits text in a scrolling terminal-style box. 
@SuppressWarnings("serial")
public class InfoPanel extends PopupWindow {
    private static final int BOX_WIDTH = 750;
    private static final int BOX_HEIGHT = 340;
    private static final int TEXT_SIZE = 18;

    private static final String HELP_TEXT = "Clear all 7 workdays by passing the paradigm quizzes and keeping your bugs below 3.\n\n"
            + "Each workday covers one paradigm. The points per question go up and the time per question "
            + "goes down every day.\n\n"
            + "A correct answer earns full points and adds to your streak. Five correct answers in a row "
            + "remove one bug.\n"
            + "A wrong answer costs half the question\u2019s points, adds 1 bug and resets the streak. "
            + "A timeout adds 1 bug and resets the streak.\n\n"
            + "The game ends when you reach 3 bugs. Your final score may earn a place on the leaderboard.";

    private static final String ABOUT_TEXT = "Welcome to the Paradigm Facility, where badly written code comes alive as terrifying "
            + "spaghetti code monsters. Contained inside locked packages, these creatures "
            + "threaten to corrupt the entire tech stack. As a new software engineer, you "
            + "must pass rigorous paradigm quizzes to unlock, refactor, and defeat them.\n\n"
            + "Survive the 7-day probationary training, keep your job, and earn your "
            + "permanent position!";

    private static final String CREDITS_TEXT = "Programmers:\n"
            + "Catenza, Dizahlene\n"
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
        text.setCaretPosition(0); // show the top of the text

        JScrollPane scroll = new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.BLACK);
        scroll.getVerticalScrollBar().setUI(new GameScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(GameScrollBarUI.WIDTH, 0));

        content.add(scroll, BorderLayout.CENTER);
    }
}
