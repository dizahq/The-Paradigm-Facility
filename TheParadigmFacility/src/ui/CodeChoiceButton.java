package ui;

import core.Answer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComponent;

/**
 * An answer button that can show code: monospaced, line breaks kept, any size.
 * ChoiceButton keeps handling plain-text answers. Same flow: show(), mark(),
 * lock().
 * The colors below are a guess at the ChoiceButton look. Match them if they
 * differ.
 */
public final class CodeChoiceButton extends JComponent {

    public enum State {
        NORMAL, CORRECT, WRONG
    }

    private static final Color FILL = new Color(30, 90, 65, 102);
    private static final Color FILL_HOVER = new Color(45, 125, 90, 140);
    private static final Color FILL_CORRECT = new Color(36, 140, 82, 210);
    private static final Color FILL_WRONG = new Color(190, 0, 0, 210);
    private static final Color LINE = new Color(135, 143, 140);
    private static final int PAD_X = 14;
    private static final int PAD_Y = 10;
    private static final int ARC = 18;

    private final Consumer<CodeChoiceButton> onClick;

    private Answer answer;
    private String text = "";
    private boolean mono;
    private boolean wrap; // short answers may wrap, code is never re-flowed
    private State state = State.NORMAL;
    private boolean hovered;
    private boolean locked;
    private CodeBlock block;

    public CodeChoiceButton(Consumer<CodeChoiceButton> onClick) {
        this.onClick = onClick;
        setOpaque(false);
        setFocusable(false);
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
            public void mouseReleased(MouseEvent e) {
                if (!locked && contains(e.getPoint())) {
                    CodeChoiceButton.this.onClick.accept(CodeChoiceButton.this);
                }
            }
        });
    }

    public Answer answer() {
        return answer;
    }

    // Shows a new answer. The font size is set afterwards with bestSize() /
    // setFontSize().
    public void show(Answer answer, String text, boolean mono, boolean wrap) {
        this.answer = answer;
        this.text = text;
        this.mono = mono;
        this.wrap = wrap;
        this.state = State.NORMAL;
        this.locked = false;
        this.hovered = false;
        this.block = null;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
    }

    public void mark(State state) {
        this.state = state;
        repaint();
    }

    // No more clicks (the result is on screen).
    public void lock() {
        locked = true;
        hovered = false;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    // The biggest size in [min, max] at which the answer fits the current bounds.
    public int bestSize(int max, int min) {
        for (int size = max; size > min; size--) {
            CodeBlock candidate = build(size);
            if (candidate.width() <= availW() && candidate.height() <= availH()) {
                return size;
            }
        }
        return min;
    }

    public void setFontSize(int size) {
        block = build(size);
        repaint();
    }

    private CodeBlock build(int size) {
        Font font = mono ? CodeFont.get(size) : Theme.bold(size);
        FontMetrics fm = getFontMetrics(font);
        List<String> lines = wrap ? CodeBlock.wrap(text, fm, availW()) : QuestionContent.lines(text);
        return new CodeBlock(fm, font, lines, null);
    }

    private int availW() {
        return getWidth() - 2 * PAD_X;
    }

    private int availH() {
        return getHeight() - 2 * PAD_Y;
    }

    private void setHovered(boolean hovered) {
        this.hovered = hovered && !locked;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(switch (state) {
            case CORRECT -> FILL_CORRECT;
            case WRONG -> FILL_WRONG;
            case NORMAL -> hovered ? FILL_HOVER : FILL;
        });
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2.setColor(state == State.NORMAL ? LINE : Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        if (block != null) {
            // code with several lines starts at the top, one or two lines sit in the middle
            int y = block.lineCount() > 2 ? PAD_Y : (getHeight() - block.height()) / 2;
            block.paint(g2, PAD_X, y, mono ? CodeBlock.TEXT : Color.WHITE, null, null);
        }
        g2.dispose();
    }
}
