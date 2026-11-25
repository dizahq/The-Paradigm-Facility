package ui;

import core.Answer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JComponent;

/**
 * One answer button of the game screen. After an answer it shows green
 * (correct) or red (wrong). Long answers scroll with the mouse wheel.
 */
final class ChoiceButton extends JComponent {

    enum State {
        NORMAL, CORRECT, WRONG
    }

    static final int WIDTH = 452;
    static final int HEIGHT = 98;

    private static final int PADDING_X = 22;
    private static final int PADDING_Y = 8;
    private static final int ANSWER_FONT = 22;
    private static final int LABEL_W = 44;

    private static final Color BG = new Color(0x08281B);
    private static final Color HOVER_BG = new Color(0x103A29);
    private static final Color CORRECT_BG = new Color(0x1B7F46);
    private static final Color WRONG_BG = new Color(0x9E2020);
    private static final Color EDGE = new Color(0xDDE3DD);

    private final String letter;
    private final TextScroll text = new TextScroll();
    private final Consumer<ChoiceButton> onChoose;

    private Answer answer;
    private State state = State.NORMAL;
    private boolean hover;
    private boolean hasText;

    ChoiceButton(String letter, Consumer<ChoiceButton> onChoose) {
        this.letter = letter;
        this.onChoose = onChoose;
        setOpaque(false);
        setFocusable(false);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setHovered(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHovered(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled() && answer != null) {
                    onChoose.accept(ChoiceButton.this);
                }
            }
        });
    }

    // Shows a new answer: normal look, clickable.
    void show(Answer newAnswer) {
        answer = newAnswer;
        state = State.NORMAL;
        hasText = true;
        text.set(this, newAnswer.getAnswer(), false, ANSWER_FONT, 1.3,
                answerWidth(), HEIGHT - 2 * PADDING_Y);
        setEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
    }

    // The answer this button shows.
    Answer answer() {
        return answer;
    }

    // Turns the button green (correct) or red (wrong).
    void mark(State newState) {
        state = newState;
        repaint();
    }

    // The button stops reacting to the mouse.
    void lock() {
        setEnabled(false);
        setCursor(Cursor.getDefaultCursor());
        setHovered(false);
    }

    // Scrolls the text (called for mouse wheel turns). dx is ignored: answers wrap.
    void scrollText(int dx, int dy) {
        text.scrollBy(dy);
        repaint();
    }

    private int answerWidth() {
        return getWidth() - 2 * PADDING_X - LABEL_W;
    }

    private void setHovered(boolean hovered) {
        hover = hovered && isEnabled();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color fill = switch (state) {
            case CORRECT -> CORRECT_BG;
            case WRONG -> WRONG_BG;
            case NORMAL -> hover && isEnabled() ? HOVER_BG : BG;
        };
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.setColor(EDGE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

        if (hasText) {
            text.paint(g2, Color.WHITE, PADDING_X + LABEL_W, PADDING_Y,
                    answerWidth(), getHeight() - 2 * PADDING_Y, true);
        }

        g2.setFont(Theme.bold(30));
        g2.setColor(Theme.ACCENT);
        FontMetrics labelMetrics = g2.getFontMetrics();
        g2.drawString(letter + ".", PADDING_X,
                (getHeight() - labelMetrics.getHeight()) / 2 + labelMetrics.getAscent());
        g2.dispose();
    }
}
