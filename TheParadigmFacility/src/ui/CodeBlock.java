package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.regex.Matcher;

/**
 * Lines of text measured and drawn with one font, line by line (code is never
 * re-flowed).
 * A run of underscores is drawn as a blank box. Its width is fixed per question
 * (wide enough for the correct answer), so nothing jumps when the answer is
 * revealed.
 */
public final class CodeBlock {

    public static final Color TEXT = new Color(0xE8EEEB);
    private static final Color BLANK_FILL = new Color(255, 255, 255, 28);
    private static final Color BLANK_LINE = new Color(0xFFD969);
    static final double LINE_SPACING = 1.35;

    private final FontMetrics fm;
    private final Font font;
    private final List<String> lines;
    private final int fillChars; // width the correct answer needs inside a blank, in characters
    private final int charWidth;
    private final int lineHeight;
    private final int width;

    CodeBlock(FontMetrics fm, Font font, List<String> lines, String blankFill) {
        this.fm = fm;
        this.font = font;
        this.lines = lines;
        this.fillChars = blankFill == null ? 0 : blankFill.length() + 2;
        this.charWidth = fm.charWidth('0');
        this.lineHeight = (int) Math.round(font.getSize2D() * LINE_SPACING);
        int widest = 0;
        for (String line : lines) {
            widest = Math.max(widest, drawLine(null, line, 0, 0, null, null, null));
        }
        this.width = widest;
    }

    // The biggest font size (from maxSize down to minSize) at which the lines fit
    // in w x h.
    public static CodeBlock fit(Component owner, IntFunction<Font> fontOf, List<String> lines, String blankFill,
            int maxSize, int minSize, int w, int h) {
        CodeBlock block = null;
        for (int size = maxSize; size >= minSize; size--) {
            Font font = fontOf.apply(size);
            block = new CodeBlock(owner.getFontMetrics(font), font, lines, blankFill);
            if (block.width() <= w && block.height() <= h) {
                break;
            }
        }
        return block;
    }

    int width() {
        return width;
    }

    int height() {
        return lines.size() * lineHeight;
    }

    int lineCount() {
        return lines.size();
    }

    // Fill: text shown inside the blank (null while it is still empty).
    public void paint(Graphics2D g, int x, int y, Color color, String fill, Color fillColor) {
        g.setFont(font);
        int firstBaseline = y + (lineHeight - fm.getHeight()) / 2 + fm.getAscent();
        for (int i = 0; i < lines.size(); i++) {
            drawLine(g, lines.get(i), x, firstBaseline + i * lineHeight, color, fill, fillColor);
        }
    }

    // Draws one line (or only measures it when g is null) and returns its right
    // edge.
    private int drawLine(Graphics2D g, String line, int startX, int baseline, Color color, String fill,
            Color fillColor) {
        Matcher blank = QuestionContent.BLANK.matcher(line);
        int cursor = 0;
        int x = startX;
        while (blank.find()) {
            x = text(g, line.substring(cursor, blank.start()), x, baseline, color);
            int boxWidth = Math.max(blank.end() - blank.start(), fillChars) * charWidth;
            if (g != null) {
                paintBlank(g, x, baseline, boxWidth, fill, fillColor);
            }
            x += boxWidth;
            cursor = blank.end();
        }
        return text(g, line.substring(cursor), x, baseline, color);
    }

    private int text(Graphics2D g, String s, int x, int baseline, Color color) {
        if (g != null && !s.isEmpty()) {
            g.setColor(color);
            g.drawString(s, x, baseline);
        }
        return x + fm.stringWidth(s);
    }

    private void paintBlank(Graphics2D g, int x, int baseline, int boxWidth, String fill, Color fillColor) {
        int top = baseline - fm.getAscent() + 1;
        int h = fm.getHeight() - 2;
        g.setColor(BLANK_FILL);
        g.fillRoundRect(x + 1, top, boxWidth - 2, h, 8, 8);
        g.setColor(BLANK_LINE);
        g.drawRoundRect(x + 1, top, boxWidth - 3, h - 1, 8, 8);
        if (fill != null) {
            g.setColor(fillColor);
            g.drawString(fill, x + (boxWidth - fm.stringWidth(fill)) / 2, baseline);
        }
    }

    // Word-wraps text to the given pixel width. Words wider than the width are cut.
    static List<String> wrap(String text, FontMetrics fm, int width) {
        List<String> out = new ArrayList<>();
        for (String paragraph : text.split("\n", -1)) {
            StringBuilder line = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (fm.stringWidth(candidate) <= width) {
                    line.setLength(0);
                    line.append(candidate);
                    continue;
                }
                if (!line.isEmpty()) {
                    out.add(line.toString());
                    line.setLength(0);
                }
                String rest = word;
                while (rest.length() > 1 && fm.stringWidth(rest) > width) {
                    int cut = rest.length() - 1;
                    while (cut > 1 && fm.stringWidth(rest.substring(0, cut)) > width) {
                        cut--;
                    }
                    out.add(rest.substring(0, cut));
                    rest = rest.substring(cut);
                }
                line.append(rest);
            }
            out.add(line.toString());
        }
        return out;
    }
}
