package ui;

// Pause Popup. Each line is clickable: continue, main menu, settings, exit
// X in title bar also continue the game.
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PausePanel extends PopupWindow {
    private static final int BOX_WIDTH = 730;
    private static final int BOX_HEIGHT = 330;

    private static final Color BACKGROUND = new Color(0x10201A);
    private static final Color ROW_HOVER = new Color(0x1B3328);
    private static final Color AMBER = new Color(0xF2B705);

    private static final String PROMPT = "game/paused> ";

    public PausePanel(Runnable onContinue, Runnable onMainMenu, Runnable onSettings, Runnable onExit) {
        super("", BOX_WIDTH, BOX_HEIGHT, BACKGROUND, onContinue);
        useLightTitleBar();
        content.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        content.add(buildHeader());
        content.add(buildRow("continue", Theme.ACCENT, onContinue));
        content.add(buildRow("main menu", AMBER, onMainMenu));
        content.add(buildRow("settings", Color.WHITE, onSettings));
        content.add(buildRow("exit", Theme.DANGER, onExit));
    }

    // First line: not clickable
    private JPanel buildHeader() {
        JPanel header = newLine();
        header.add(text("click/what/you/want/to/do> game paused", Color.WHITE));
        return header;
    }

    // A clickable line: prompt in white, command in its own color
    private JPanel buildRow(String command, Color color, Runnable onClick) {
        JPanel row = newLine();
        row.setOpaque(true);
        row.setBackground(BACKGROUND);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.add(text(PROMPT, Color.WHITE));
        row.add(text(command, color));

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(ROW_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(BACKGROUND);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        return row;
    }

    // An empty left-aligned line that keeps its natural height
    private JPanel newLine() {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        line.setOpaque(false);
        line.setAlignmentX(Component.LEFT_ALIGNMENT);

        // lines are added to content afterwards, so size from sample label
        int height = new JLabel("X").getFontMetrics(Theme.bold(18)).getHeight() + 6;
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return line;
    }

    private static JLabel text(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.bold(18));
        label.setForeground(color);
        return label;
    }
}