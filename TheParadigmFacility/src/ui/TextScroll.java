package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

/** Wraps text at a fixed font size and scrolls it when it is taller than its box. */
final class TextScroll {

    static final int BAR_WIDTH = 5;
    private static final int BAR_GAP = 4;
    private static final Color TRACK = new Color(255, 255, 255, 30);
    private static final Color THUMB = new Color(255, 255, 255, 140);

    private FontMetrics fm;
    private Font font = Theme.plain(16);
    private List<String> lines = List.of();
    private int pitch;
    private int contentH;
    private int viewH;
    private int offset;

    /** Wraps the text for a box. The scroll position goes back to the top. */
    void set(Component owner, String text, boolean bold, int size, double pitchFactor,
            int boxWidth, int boxHeight) {
        font = bold ? Theme.bold(size) : Theme.plain(size);
        fm = owner.getFontMetrics(font);
        pitch = (int) Math.round(size * pitchFactor);
        // the bar's width is always reserved, so the wrapping never changes while scrolling
        lines = TextFit.wrap(text, fm, boxWidth - BAR_WIDTH - BAR_GAP);
        contentH = (lines.size() - 1) * pitch + fm.getAscent() + fm.getDescent();
        viewH = boxHeight;
        offset = 0;
    }

    boolean canScroll() {
        return contentH > viewH;
    }

    void scrollBy(int pixels) {
        offset = Math.max(0, Math.min(Math.max(0, contentH - viewH), offset + pixels));
    }

    /** Draws the visible part of the text inside the box. */
    void paint(Graphics2D g2, Color color, int x, int y, int width, int height, boolean centered) {
        if (fm == null) {
            return;
        }
        var oldClip = g2.getClip();
        g2.clipRect(x, y, width, height);

        g2.setFont(font);
        g2.setColor(color);
        int area = canScroll() ? width - BAR_WIDTH - BAR_GAP : width;
        int top = canScroll() ? y - offset : (centered ? y + (height - contentH) / 2 : y);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineX = centered ? x + (area - fm.stringWidth(line)) / 2 : x;
            g2.drawString(line, lineX, top + fm.getAscent() + i * pitch);
        }
        g2.setClip(oldClip);

        if (canScroll()) {
            drawBar(g2, true, x + width - BAR_WIDTH, y, height, contentH, viewH, offset);
        }
    }

    /** A thin scrollbar. length = size of the track, contentLen and viewLen = size of the content and of the view. */
    static void drawBar(Graphics2D g2, boolean vertical, int x, int y, int length,
            int contentLen, int viewLen, int offset) {
        int thumb = Math.min(length, Math.max(24, length * viewLen / contentLen));
        int travel = length - thumb;
        int pos = contentLen <= viewLen ? 0 : travel * offset / (contentLen - viewLen);

        g2.setColor(TRACK);
        if (vertical) {
            g2.fillRoundRect(x, y, BAR_WIDTH, length, BAR_WIDTH, BAR_WIDTH);
            g2.setColor(THUMB);
            g2.fillRoundRect(x, y + pos, BAR_WIDTH, thumb, BAR_WIDTH, BAR_WIDTH);
        } else {
            g2.fillRoundRect(x, y, length, BAR_WIDTH, BAR_WIDTH, BAR_WIDTH);
            g2.setColor(THUMB);
            g2.fillRoundRect(x + pos, y, thumb, BAR_WIDTH, BAR_WIDTH, BAR_WIDTH);
        }
    }
}