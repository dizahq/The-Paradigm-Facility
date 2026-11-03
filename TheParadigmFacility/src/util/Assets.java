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

// Only class that knows where the game's files live: TheParadigmFacility/assets/ and /data/
public class Assets {
    // Tried in order, so it works from the repo root, from TheParadigmFacility/, or
    // from src/
    private static final String[] ROOTS = { "TheParadigmFacility/", "", "../", "../TheParadigmFacility/" };

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    private Assets() {
    }

    private static File findDir(String sub) {
        for (String root : ROOTS) {
            File dir = new File(root + sub);
            if (dir.isDirectory()) {
                return dir;
            }
        }
        return new File(ROOTS[0] + sub); // default, so dataFile() can still create it
    }

    // Opens a file under assets/, null if not found
    public static InputStream open(String path) {
        InputStream in = Assets.class.getResourceAsStream("/assets/" + path);
        if (in != null) {
            return in;
        }
        try {
            return new FileInputStream(new File(findDir("assets"), path));
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
            System.err.println("Failed reading " + path + ": " + e);
        }
        if (img == null) {
            System.err.println("Image not found: " + path
                    + " (working dir: " + new File("").getAbsolutePath() + ")");
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
        File file = new File(findDir("data"), name);
        File dir = file.getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }
        return file;
    }
}