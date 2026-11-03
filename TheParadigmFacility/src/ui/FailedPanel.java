package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** "FAILED" screen made entirely from pictures. */
public class FailedPanel extends JPanel {

    private static final String ASSETS = "TheParadigmFacility/assets/interface/";
    private static final String MESSAGE_PATH = ASSETS + "failed_message.png"; // whole text block
    private static final String IMAGE_PATH = ASSETS + "failed_monster.png"; // creature and box
    private static final String MAINMENU_PATH = ASSETS + "button_mainmenu.png";
    private static final String EXIT_PATH = ASSETS + "button_exit.png";
    private static final String LEVELSELECT_PATH = ASSETS + "button_levelselect.png";
    private static final Color TINT = new Color(40, 10, 10, 200); // dark red-brown; last number = opacity (0-255)

    // Widths in pixels. Heights follow each picture's proportions.
    private static final int MESSAGE_WIDTH = 650;
    private static final int IMAGE_WIDTH = 260;
    private static final int BUTTON_WIDTH = 170;

    // Spacing
    private static final int GAP_PICTURE = 20; // message -> creature picture
    private static final int GAP_BUTTONS = 30; // creature picture -> buttons
    private static final int BUTTON_SPACING = 14;

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
     * Loads a picture, scales it to the given width, and returns it as a centered
     * label.
     */
    private JComponent picture(String path, int width) {
        File file = new File(path);
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null)
                throw new java.io.IOException("Unsupported image");

            int h = img.getHeight() * width / img.getWidth();
            BufferedImage scaled = new BufferedImage(width, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(img, 0, 0, width, h, null);
            g2.dispose();
            label.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            System.err.println("Image not found at: " + file.getAbsolutePath());
        }
        return label;
    }
}