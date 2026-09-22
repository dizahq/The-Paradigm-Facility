package ui;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/** A button drawn from a picture. Dim normally, bright and slightly larger on hover. */
public class ImageButton extends JComponent {

    private BufferedImage image;
    private final String missingLabel;
    private boolean hover;

    public ImageButton(String path, int width, Runnable onClick) {
        this.missingLabel = path;
        File file = new File(path);
        try {
            image = ImageIO.read(file);
        } catch (Exception e) {
            System.err.println("Button image not found at: " + file.getAbsolutePath());
        }

        int w = width;
        int h = 60; // fallback height if the image is missing
        if (image != null) {
            h = image.getHeight() * width / image.getWidth();
        }
        // Extra room so the hover enlargement is not clipped
        Dimension size = new Dimension(w + 20, h + 12);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, hover ? 1.0f : 0.8f));

        int baseW = getWidth() - 20;
        int baseH = getHeight() - 12;
        double s = hover ? 1.05 : 1.0;
        int w = (int) (baseW * s);
        int h = (int) (baseH * s);
        int x = (getWidth() - w) / 2;
        int y = (getHeight() - h) / 2;

        g2.drawImage(image, x, y, w, h, null);
        g2.dispose();
    }
}