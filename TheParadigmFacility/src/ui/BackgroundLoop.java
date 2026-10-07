package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Swing/AWT background loop. This replaces the JavaFX video background and works
 * with plain Java Swing without any special module configuration.
 */
public class BackgroundLoop extends JPanel {

    private static final String DEFAULT_BACKGROUND_PATH =
            "TheParadigmFacility/assets/background/background.mp4";

    private BufferedImage background;
    private BufferedImage scaledBackground;
    private int offsetX;
    private int offsetY;
    private final Timer timer;

    public BackgroundLoop() {
        this(DEFAULT_BACKGROUND_PATH);
    }

    public BackgroundLoop(String imagePath) {
        setOpaque(false);
        setPreferredSize(new Dimension(1280, 720));
        loadBackground(imagePath);

        timer = new Timer(30, e -> {
            offsetX = (offsetX + 1) % 25;
            offsetY = (offsetY + 1) % 25;
            repaint();
        });
        timer.start();
    }

    private void loadBackground(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Background asset not found at: " + file.getAbsolutePath());
                return;
            }

            if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".mov")) {
                System.err.println("Video file detected. Swing background loop does not play MP4 directly. " +
                        "Falling back to a solid black background. Place a still image or GIF instead.");
                background = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = background.createGraphics();
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, 1280, 720);
                g2.dispose();
                return;
            }

            background = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
            background = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = background.createGraphics();
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, 1280, 720);
            g2.dispose();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (background != null) {
            int w = getWidth();
            int h = getHeight();

            if (scaledBackground == null || scaledBackground.getWidth() != w || scaledBackground.getHeight() != h) {
                scaledBackground = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D bg2 = scaledBackground.createGraphics();
                bg2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                bg2.drawImage(background, 0, 0, w, h, null);
                bg2.dispose();
            }

            int x = offsetX;
            int y = offsetY;

            g2.drawImage(scaledBackground, x, y, null);
            if (x > 0) {
                g2.drawImage(scaledBackground, x - w, y, null);
            }
            if (y > 0) {
                g2.drawImage(scaledBackground, x, y - h, null);
            }
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
    }

    public void dispose() {
        timer.stop();
    }
}
