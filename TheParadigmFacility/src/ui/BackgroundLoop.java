package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
import util.Assets;

public class BackgroundLoop extends JPanel {

    private static final String DEFAULT_BACKGROUND_PATH = "background/background.png";
    private static final int FRAME_WIDTH = 480;
    private static final int FRAME_HEIGHT = 270;
    private static final int COLUMNS = 10;
    private static final int ROWS = 9;
    private static final int TOTAL_FRAMES = COLUMNS * ROWS;
    private static final int PLAYBACK_SPEED = 30; // milliseconds per frame

    private final BufferedImage[] frames = new BufferedImage[TOTAL_FRAMES];
    private boolean loaded;
    private int currentFrameIndex;
    private Timer timer;

    public BackgroundLoop() {
        setOpaque(false);
        setPreferredSize(new Dimension(1280, 720));
        loadFrames(DEFAULT_BACKGROUND_PATH);

        if (loaded) {
            timer = new Timer(PLAYBACK_SPEED, e -> {
                currentFrameIndex = (currentFrameIndex + 1) % TOTAL_FRAMES;
                repaint();
            });

            timer.start();
        }
    }

    private void loadFrames(String path) {
        BufferedImage spriteSheet = Assets.image(path);
        if (spriteSheet == null) {
            loaded = false;
            return;
        }

        for (int i = 0; i < TOTAL_FRAMES; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            frames[i] = spriteSheet.getSubimage(col * FRAME_WIDTH, row * FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);
        }

        loaded = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!loaded)
            return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (loaded) {
            BufferedImage currentFrame = frames[currentFrameIndex];
            g2d.drawImage(currentFrame, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.dispose();
    }

    public void dispose() {
        if (timer != null) {
            timer.stop();
        }
    }
}
