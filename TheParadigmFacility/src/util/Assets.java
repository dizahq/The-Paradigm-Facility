package util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public final class Assets {

    private static final String[] DEV_ROOTS = { "", "TheParadigmFacility/" };
    private static final String SAVE_FOLDER = "ParadigmFacility";

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    private Assets() {
    }

    // Opens a file under assets/. Returns null if it is not found
    public static InputStream open(String path) {
        return openResource("assets/" + path);
    }

    // Opens a file under data/. Returns null if it is not found.
    public static InputStream openData(String name) {
        return openResource("data/" + name);
    }

    private static InputStream openResource(String path) {
        InputStream in = Assets.class.getResourceAsStream("/" + path);
        if (in != null) {
            return in;
        }
        for (String root : DEV_ROOTS) {
            File file = new File(root + path);
            if (file.isFile()) {
                try {
                    return new FileInputStream(file);
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    // Loads an image under assets/. Returns null if it is missing.
    public static BufferedImage image(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }
        BufferedImage img = null;
        try (InputStream in = open(path)) {
            if (in != null) {
                img = ImageIO.read(in);
            }
        } catch (IOException e) {
            System.err.println("Could not read image " + path + ": " + e.getMessage());
        }
        if (img == null) {
            System.err.println("Image not found: " + path);
        }
        cache.put(path, img);
        return img;
    }

    // Same as image(), scaled to the given width with the aspect ratio kept.
    public static BufferedImage scaled(String path, int width) {
        String key = path + "@" + width;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        BufferedImage src = image(path);
        if (src == null) {
            return null;
        }
        int height = src.getHeight() * width / src.getWidth();
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        cache.put(key, out);
        return out;
    }

    // A writable file in the game's save folder. The folder is created if needed.
    public static File saveFile(String name) {
        File dir = new File(System.getProperty("user.home"), SAVE_FOLDER);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            System.err.println("Could not create save folder: " + dir);
        }
        return new File(dir, name);
    }
}
