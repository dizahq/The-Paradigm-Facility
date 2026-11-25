package ui;

import core.Answer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape; // NEW
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComponent;

/**
 * An answer button that can show code: monospaced, line breaks kept.
 * Content bigger than the button scrolls (mouse wheel, Shift + wheel for sideways).
 * ChoiceButton keeps handling plain-text answers. Same flow: show(), mark(), lock().
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
    private static final int LABEL_W = 34;
    private static final int ARC = 18;
    private static final int BAR_INSET = 9; // scrollbars sit inside the padding
    private static final int SCROLL_STEP = 40; // NEW: pixels per mouse wheel notch

    private static final int SIZE_STACKED = 20; // short answers beside the code (wrap = true)
    private static final int SIZE_GRID = 18; // code boxes in the 2x2 grid

    private final String letter;
    private final Consumer<CodeChoiceButton> onClick;

    private Answer answer;
    private String text = "";
    private boolean mono;
    private boolean wrap; // short answers may wrap, code is never re-flowed
    private State state = State.NORMAL;
    private boolean hovered;
    private boolean locked;
    private CodeBlock block;
    private int offsetX;
    private int offsetY;

    public CodeChoiceButton(String letter, Consumer<CodeChoiceButton> onClick) {
        this.letter = letter;
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

        // NEW: the wheel scrolls up/down; Shift + wheel scrolls sideways. If the content
        // only overflows in one direction, the wheel scrolls that direction.
        addMouseWheelListener(e -> {
            int pixels = (int) Math.round(e.getPreciseWheelRotation() * SCROLL_STEP);
            boolean sideways = e.isShiftDown() ? overflowsX() : (!overflowsY() && overflowsX());
            if (sideways) {
                scrollText(pixels, 0);
            } else {
                scrollText(0, pixels);
            }
        });
    }

    public Answer answer() {
        return answer;
    }

    // Shows a new answer at a fixed font size. The bounds must be set before this call.
    public void show(Answer answer, String text, boolean mono, boolean wrap) {
        this.answer = answer;
        this.text = text;
        this.mono = mono;
        this.wrap = wrap;
        this.state = State.NORMAL;
        this.locked = false;
        this.hovered = false;
        this.offsetX = 0;
        this.offsetY = 0;
        this.block = build(wrap ? SIZE_STACKED : SIZE_GRID);
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

    // Scrolls the content (called for mouse wheel turns).
    public void scrollText(int dx, int dy) {
        if (block == null) {
            return;
        }
        offsetX = Math.max(0, Math.min(Math.max(0, block.width() - availW()), offsetX + dx));
        offsetY = Math.max(0, Math.min(Math.max(0, block.height() - availH()), offsetY + dy));
        repaint();
    }

    private boolean overflowsX() {
        return block != null && block.width() > availW();
    }

    private boolean overflowsY() {
        return block != null && block.height() > availH();
    }

    private CodeBlock build(int size) {
        Font font = mono ? CodeFont.get(size) : Theme.bold(size);
        FontMetrics fm = getFontMetrics(font);
        List<String> lines = wrap ? CodeBlock.wrap(text, fm, availW()) : QuestionContent.lines(text);
        return new CodeBlock(fm, font, lines, null);
    }

    private int availW() {
        return getWidth() - 2 * PAD_X - LABEL_W;
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

        g2.setFont(Theme.bold(22));
        g2.setColor(Theme.ACCENT);
        FontMetrics lm = g2.getFontMetrics();
        g2.drawString(letter + ".", PAD_X, (getHeight() - lm.getHeight()) / 2 + lm.getAscent());

        if (block != null) {
            boolean scrollX = overflowsX();
            boolean scrollY = overflowsY();
            int x = PAD_X + LABEL_W;
            // code with several lines (or anything that scrolls) starts at the top,
            // one or two lines sit in the middle
            int y = scrollY || block.lineCount() > 2 ? PAD_Y : (getHeight() - block.height()) / 2;

            // text is cut at the edges of the content area, so it never covers the letter
            Shape oldClip = g2.getClip();
            g2.clipRect(x, PAD_Y, availW(), availH());
            block.paint(g2, x - offsetX, y - offsetY, mono ? CodeBlock.TEXT : Color.WHITE, null, null);
            g2.setClip(oldClip);

            if (scrollY) {
                TextScroll.drawBar(g2, true, getWidth() - BAR_INSET, PAD_Y, availH(), block.height(), availH(),
                        offsetY);
            }
            if (scrollX) {
                TextScroll.drawBar(g2, false, x, getHeight() - BAR_INSET, availW(), block.width(), availW(),
                        offsetX);
            }
        }
        g2.dispose();
    }
}