package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Overlay asking the player to type their name.
 * onSubmit receives the name once they confirm. onClose is called if they press X.
 */
public class NamePanel extends JPanel {

    private static final Color BACKDROP = new Color(0, 0, 0, 160);
    private static final Color TITLEBAR_BG = new Color(0x4A4A4A);
    private static final Color PANEL_BG = new Color(0x0C2119);
    private static final Color FIELD_BG = new Color(0x6B7C72);
    private static final Color ACCENT = new Color(0xB22222);

    private static final int BOX_WIDTH = 620;
    private static final int BOX_HEIGHT = 340;

    public NamePanel(Consumer<String> onSubmit, Runnable onClose) {
        setOpaque(false);
        setLayout(new GridBagLayout()); // centers the window box

        JPanel windowBox = new JPanel(new BorderLayout());
        windowBox.setPreferredSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
        windowBox.setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
        windowBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        windowBox.add(buildTitleBar(onClose), BorderLayout.NORTH);
        windowBox.add(buildContent(onSubmit), BorderLayout.CENTER);

        add(windowBox);
    }

    private JComponent buildTitleBar(Runnable onClose) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(TITLEBAR_BG);
        bar.setPreferredSize(new Dimension(BOX_WIDTH, 40));

        CloseX close = new CloseX(onClose);
        JPanel closeWrap = new JPanel();
        closeWrap.setOpaque(false);
        closeWrap.add(close);

        bar.add(closeWrap, BorderLayout.EAST);
        return bar;
    }

    private JComponent buildContent(Consumer<String> onSubmit) {
        JPanel content = new JPanel();
        content.setBackground(PANEL_BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel heading = new JLabel("Welcome to Paradigm Facility");
        heading.setFont(new Font("Consolas", Font.BOLD, 26));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("\u2022 /* Enter your name to start */");
        subtitle.setFont(new Font("Consolas", Font.PLAIN, 18));
        subtitle.setForeground(new Color(0x8FA096));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField field = new JTextField();
        field.setFont(new Font("Consolas", Font.PLAIN, 16));
        field.setBackground(FIELD_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        field.setMaximumSize(new Dimension(BOX_WIDTH, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        ActionListener submit = e -> {
            String name = field.getText().trim();
            if (!name.isEmpty()) {
                onSubmit.accept(name);
            }
        };
        field.addActionListener(submit); // Enter key submits

        content.add(heading);
        content.add(Box.createVerticalStrut(10));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(40));
        content.add(field);

        return content;
    }

    /** Dim everything behind the window box. */
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(BACKDROP);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    /** Red "X" close button, top right of the title bar. */
    private static class CloseX extends JComponent {
        private boolean hover;

        CloseX(Runnable onClick) {
            Dimension size = new Dimension(36, 36);
            setPreferredSize(size);
            setMaximumSize(size);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hover ? Color.WHITE : ACCENT);
            g2.setFont(new Font("Consolas", Font.BOLD, 20));
            g2.setStroke(new BasicStroke(2f));

            String text = "X";
            int tw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (getWidth() - tw) / 2, getHeight() / 2 + 7);
            g2.dispose();
        }
    }
}