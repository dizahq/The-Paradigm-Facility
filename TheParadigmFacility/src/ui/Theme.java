package ui;

// Shared colors and fonts. Change a value here and it changes everywhere.

import java.awt.Color;
import java.awt.Font;

public final class Theme {
    // Backgrounds
    public static final Color BACKDROP = new Color(0, 0, 0, 160); // dim layer behind popups
    public static final Color TITLEBAR = new Color(0x4A4A4A);
    public static final Color PANEL = new Color(0x0C2119);
    public static final Color FIELD = new Color(0x6B7C72);

    // Text and lines
    public static final Color MUTED = new Color(0x8FA096); // subtitles, hints
    public static final Color LINE = new Color(255, 255, 255, 60); // dividers, switch off

    // Accents
    public static final Color ACCENT = new Color(0x4ADE80); // green: on, hover
    public static final Color DANGER = new Color(0xB22222); // red: close X
    public static final Color HOVER_RED = new Color(0xFF4D4D); // play button hover
    public static final Color IDLE = new Color(0x6B746B); // play button normal

    public static final String FONT = "Consolas";

    private Theme() {
    }

    public static Font plain(int size) {
        return new Font(FONT, Font.PLAIN, size);
    }

    public static Font bold(int size) {
        return new Font(FONT, Font.BOLD, size);
    }
}
