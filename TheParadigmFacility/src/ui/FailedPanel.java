package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.Assets;

/** "FAILED" screen made entirely from pictures. */
public class FailedPanel extends JPanel {

    // Paths are inside TheParadigmFacility/assets/ (Assets adds the rest)
    private static final String ASSETS = "interface/";
    private static final String MESSAGE_PATH = ASSETS + "failed_message.png"; // whole text block
    private static final String IMAGE_PATH = ASSETS + "failed_monster.png"; // creature and box
    private static final String MAINMENU_PATH = ASSETS + "failed_mainmenu.png";
    private static final String EXIT_PATH = ASSETS + "failed_exit.png";
    private static final String LEVELSELECT_PATH = ASSETS + "failed_levelselect.png";
    private static final Color TINT = new Color(40, 10, 10, 90); // dark red-brown; last number = opacity (0-255)

    // Widths in pixels. Heights follow each picture's proportions.
    private static final int MESSAGE_WIDTH = 650;
    private static final int IMAGE_WIDTH = 500;
    private static final int BUTTON_WIDTH = 230;

    // Spacing
    private static final int GAP_PICTURE = 10; // message -> creature picture
    private static final int GAP_BUTTONS = 5; // creature picture -> buttons
    private static final int BUTTON_SPACING = 24; // space between buttons

    public FailedPanel(Runnable onMainMenu, Runnable onExit, Runnable onLevelSelect) {
        setOpaque(false);
        setLayout(new GridBagLayout()); // centers the column

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        column.add(picture(MESSAGE_PATH, MESSAGE_WIDTH));
        column.add(Box.createVerticalStrut(GAP_PICTURE));
        column.add(picture(IMAGE_PATH, IMAGE_WIDTH));
        column.add(Box.createVerticalStrut(GAP_BUTTONS));
        column.add(buttonRow(onMainMenu, onExit, onLevelSelect));

        add(column);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(TINT);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    private JPanel buttonRow(Runnable onMainMenu, Runnable onExit, Runnable onLevelSelect) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, BUTTON_SPACING, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.add(new ImageButton(MAINMENU_PATH, BUTTON_WIDTH, onMainMenu));
        row.add(new ImageButton(EXIT_PATH, BUTTON_WIDTH, onExit));
        row.add(new ImageButton(LEVELSELECT_PATH, BUTTON_WIDTH, onLevelSelect));
        return row;
    }

    /**
     * Loads a picture (cached by Assets), scaled to the given width, as a centered
     * label.
     */
    private JComponent picture(String path, int width) {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        BufferedImage img = Assets.scaled(path, width);
        if (img != null) {
            label.setIcon(new ImageIcon(img));
        }
        return label;
    }
}