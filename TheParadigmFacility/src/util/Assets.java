package util;

// Only class that knows where the game's files live: TheParadigmFacility/assets/
// Important for JAR/exe packaging

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

public class Assets {
    private static final String PROJECT_DIR = "TheParadigmFacility/";
    private static final String ASSETS_DIR = PROJECT_DIR + "assets/";
    private static final String DATA_DIR = PROJECT_DIR + "data/";

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    private Assets() {
    }

    // Opens a file under assets/, null if not found
    public static InputStream open(String path) {
        InputStream in = Assets.class.getResourceAsStream("/assets/" + path);
        if (in != null) {
            return in;
        }
        try {
            return new FileInputStream(ASSETS_DIR + path);
        } catch (IOException e) {
            return null;
        }
    }

    // Loads an image under assets/, null if missing
    public static BufferedImage image(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }
        BufferedImage img = null;
        try (InputStream in = open(path)) {
            if (in != null) {
                img = ImageIO.read(in);
            }
        } catch (Exception e) {
        }
        if (img == null) {
            System.err.println("Image not found: " + path);
        }
        cache.put(path, img);
        return img;
    }

    // Same as image(), scaled to the given width, keeping the aspect ratio
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

    // A file inside data/, e.g. "scoreboard.csv". Folder is created if missing.
    public static File dataFile(String name) {
        File file = new File(DATA_DIR + name);
        File dir = file.getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }
        return file;
    }
}
