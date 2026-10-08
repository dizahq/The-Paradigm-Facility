package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JComponent;

public class ToggleSwitch extends JComponent {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 36;
    private static final int BORDER = 2;
    private static final int KNOB_GAP = 4; // space between the border and knob

    private boolean on;

    public ToggleSwitch(boolean initial, Consumer<Boolean> onToggle) {
        this.on = initial;

        Dimension size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);
        setMaximumSize(size);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                on = !on;
                onToggle.accept(on);
                repaint();
            }
        });
    }

    public boolean isOn() {
        return on;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Color color = on ? Theme.ACCENT : Theme.MUTED;

        // track
        g2.setColor(Theme.PANEL);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(color);
        g2.setStroke(new BasicStroke(BORDER));
        g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2);

        // square knob
        int inset = BORDER + KNOB_GAP;
        int knob = getHeight() - 2 * inset;
        int x = on ? getWidth() - inset - knob : inset;
        g2.fillRect(x, inset, knob, knob);

        g2.dispose();
    }
}
