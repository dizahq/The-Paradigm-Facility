package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import util.Assets;

public class ScoreManager {

    private static final File SCORES_FILE = Assets.dataFile("scoreboard.csv");

    private final Map<String, Integer> scores = new LinkedHashMap<>();

    public ScoreManager() {
        loadScores();
    }

    private void loadScores() {
        File file = SCORES_FILE;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create scores file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        scores.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading scores file: " + e.getMessage());
        }
    }

    public void recordPlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String cleanName = name.trim();
        if (!scores.containsKey(cleanName)) {
            scores.put(cleanName, 0);
            saveScores();
        }
    }

    private void saveScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error writing scores file: " + e.getMessage());
        }
    }

    public int getScore(String name) {
        return scores.getOrDefault(name, 0);
    }

    public void setScore(String name, int score) {
        scores.put(name, score);
        saveScores();
    }
}