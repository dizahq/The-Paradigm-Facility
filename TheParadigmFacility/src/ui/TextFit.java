package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

// Wraps text into lines and shrinks the font until the text fits a box. 
final class TextFit {

    record Fit(Font font, List<String> lines, int pitch) {
    }

    private TextFit() {
    }

    /*
     * Finds the biggest font size (from maxSize down to minSize) at which the text,
     * wrapped to
     * boxWidth, fits boxHeight. If even minSize does not fit, minSize is used.
     * pitchFactor: distance between baselines as a multiple of the font size.
     */
    static Fit fit(Component owner, String text, boolean bold, int maxSize, int minSize,
            double pitchFactor, int boxWidth, int boxHeight) {
        Fit fit = null;
        for (int size = maxSize; size >= minSize; size--) {
            Font font = bold ? Theme.bold(size) : Theme.plain(size);
            FontMetrics fm = owner.getFontMetrics(font);
            fit = new Fit(font, wrap(text, fm, boxWidth), (int) Math.round(size * pitchFactor));
            if (blockHeight(fit, fm) <= boxHeight) {
                break;
            }
        }
        return fit;
    }

    /*
     * Draws the fitted text inside the box at (x, y, width, height).
     * centered: centered both ways. Otherwise left aligned at the top of the box.
     */
    static void draw(Graphics2D g2, Fit fit, Color color, int x, int y, int width, int height,
            boolean centered) {
        g2.setFont(fit.font());
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();

        int top = centered ? y + (height - blockHeight(fit, fm)) / 2 : y;
        for (int i = 0; i < fit.lines().size(); i++) {
            String line = fit.lines().get(i);
            int lineX = centered ? x + (width - fm.stringWidth(line)) / 2 : x;
            g2.drawString(line, lineX, top + fm.getAscent() + i * fit.pitch());
        }
    }

    private static int blockHeight(Fit fit, FontMetrics fm) {
        return (fit.lines().size() - 1) * fit.pitch() + fm.getAscent() + fm.getDescent();
    }

    // Splits text into lines no wider than maxWidth. Line breaks (\n) are kept.
    private static List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.split("\n", -1)) {
            StringBuilder line = new StringBuilder();
            for (String word : paragraph.split(" ", -1)) {
                // a word wider than the box is cut into pieces
                while (word.length() > 1 && fm.stringWidth(word) > maxWidth) {
                    if (line.length() > 0) {
                        lines.add(line.toString());
                        line.setLength(0);
                    }
                    int cut = 1;
                    while (cut < word.length() && fm.stringWidth(word.substring(0, cut + 1)) <= maxWidth) {
                        cut++;
                    }
                    lines.add(word.substring(0, cut));
                    word = word.substring(cut);
                }

                if (line.length() == 0) {
                    line.append(word);
                } else if (fm.stringWidth(line + " " + word) <= maxWidth) {
                    line.append(' ').append(word);
                } else {
                    lines.add(line.toString());
                    line.setLength(0);
                    line.append(word);
                }
            }
            lines.add(line.toString());
        }
        return lines;
    }
}