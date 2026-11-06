package ui;

import core.Answer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JComponent;

/**
 * One answer button of the game screen. After an answer it shows green
 * (correct) or red (wrong).
 */
final class ChoiceButton extends JComponent {

    enum State {
        NORMAL, CORRECT, WRONG
    }

    static final int WIDTH = 452;
    static final int HEIGHT = 98;

    private static final int PADDING_X = 22;
    private static final int PADDING_Y = 8;

    private static final Color BG = new Color(0x08281B);
    private static final Color HOVER_BG = new Color(0x103A29);
    private static final Color CORRECT_BG = new Color(0x1B7F46);
    private static final Color WRONG_BG = new Color(0x9E2020);
    private static final Color EDGE = new Color(0xDDE3DD);

    private Answer answer;
    private State state = State.NORMAL;
    private boolean hover;
    private TextFit.Fit fit;

    ChoiceButton(Consumer<ChoiceButton> onChoose) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

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
        fit = TextFit.fit(this, newAnswer.getAnswer(), false, 28, 14, 1.3,
                WIDTH - 2 * PADDING_X, HEIGHT - 2 * PADDING_Y);
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
        hover = false;
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

        if (fit != null) {
            TextFit.draw(g2, fit, Color.WHITE, PADDING_X, PADDING_Y,
                    getWidth() - 2 * PADDING_X, getHeight() - 2 * PADDING_Y, true);
        }
        g2.dispose();
    }
}