package ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** The monospaced font used for every code snippet and code answer. */
public final class CodeFont {

    /** First installed family wins. Bundle one with the game if you need the same look everywhere. */
    private static final String[] PREFERRED = {
            "JetBrains Mono", "Cascadia Mono", "Consolas", "Menlo", "DejaVu Sans Mono" };
    private static final String FAMILY = pickFamily();

    private CodeFont() {
    }

    public static Font get(int size) {
        return new Font(FAMILY, Font.PLAIN, size);
    }

    private static String pickFamily() {
        Set<String> installed = new HashSet<>(
                Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String family : PREFERRED) {
            if (installed.contains(family)) {
                return family;
            }
        }
        return Font.MONOSPACED;
    }
}
