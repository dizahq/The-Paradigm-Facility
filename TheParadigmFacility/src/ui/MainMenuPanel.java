package ui;

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Main menu: title on top, three picture buttons centered below. */
public class MainMenuPanel extends JPanel {

    private static final String ASSETS = "TheParadigmFacility/assets/interface/";
    private static final String TITLE_PATH   = ASSETS + "title2.png";
    private static final String START_PATH   = ASSETS + "startbtn.png";
    private static final String OPTIONS_PATH = ASSETS + "settingsbtn.png";
    private static final String QUIT_PATH    = ASSETS + "exitbtn.png";

    private static final int TITLE_WIDTH = 1000;   // title size
    private static final int BUTTON_WIDTH = 400;  // width of every button
    private static final int TITLE_GAP = 80;      // space between title and first button
    private static final int BUTTON_GAP = 10;     // space between buttons

    public MainMenuPanel(Runnable onStart, Runnable onOptions, Runnable onQuit) {
        setOpaque(false);
        setLayout(new GridBagLayout()); // centers the column in the window

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        JLabel title = buildTitle();
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageButton start = new ImageButton(START_PATH, BUTTON_WIDTH, onStart);
        ImageButton options = new ImageButton(OPTIONS_PATH, BUTTON_WIDTH, onOptions);
        ImageButton quit = new ImageButton(QUIT_PATH, BUTTON_WIDTH, onQuit);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        options.setAlignmentX(Component.CENTER_ALIGNMENT);
        quit.setAlignmentX(Component.CENTER_ALIGNMENT);

        column.add(title);
        column.add(Box.createVerticalStrut(TITLE_GAP));
        column.add(start);
        column.add(Box.createVerticalStrut(BUTTON_GAP));
        column.add(options);
        column.add(Box.createVerticalStrut(BUTTON_GAP));
        column.add(quit);

        add(column);
    }

    private JLabel buildTitle() {
        File file = new File(TITLE_PATH);
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) throw new java.io.IOException("Unsupported image");

            int w = TITLE_WIDTH;
            int h = img.getHeight() * w / img.getWidth();
            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(img, 0, 0, w, h, null);
            g2.dispose();
            return new JLabel(new ImageIcon(scaled));
        } catch (Exception e) {
            System.err.println("Title not found at: " + file.getAbsolutePath());
            JLabel fallback = new JLabel("PARADIGM FACILITY");
            fallback.setFont(new Font("Consolas", Font.BOLD, 56));
            fallback.setForeground(Color.WHITE);
            return fallback;
        }
    }
}